package com.gmail.subnokoii78.gpcore.database.sqlite;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * PaperMCにはSqliteが標準で組み込まれているのでshadowやloaderを使う必要はない
 */
@NullMarked
public class SqliteDatabase {
    private final String url;

    @Nullable
    private Connection connection;

    protected SqliteDatabase(Path path) {
        this.url = String.format("jdbc:sqlite:%s", path.toAbsolutePath());
    }

    protected Connection getConnection() throws SqliteDatabaseException {
        if (connection == null) {
            throw new SqliteDatabaseException("データベースに接続されていません");
        }
        return connection;
    }

    protected void connect() {
        if (connection != null) {
            throw new SqliteDatabaseException("データベースへの接続に失敗しました: 既に接続されています");
        }

        try {
            connection = DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("データベースへの接続に失敗しました: ", e);
        }
    }

    protected void disconnect() {
        if (connection == null) {
            throw new SqliteDatabaseException("切断に失敗しました: 既に接続されていません");
        }

        try {
            connection.close();
            connection = null;
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("切断に失敗しました: ", e);
        }
    }
}
