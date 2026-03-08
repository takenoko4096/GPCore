package com.gmail.subnokoii78.gpcore.database.sqlite;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

/**
 * PaperMCにはSqliteが標準で組み込まれているのでshadowやloaderを使う必要はない
 */
@NullMarked
public abstract class SqliteDatabase {
    private final Path path;

    private final String url;

    @Nullable
    private Connection connection;

    protected SqliteDatabase(Path path) {
        this.path = path;
        this.url = String.format("jdbc:sqlite:%s", path.toAbsolutePath());
    }

    public final Path getPath() {
        return path;
    }

    protected final Connection getConnection() throws SqliteDatabaseException {
        if (connection == null) {
            throw new SqliteDatabaseException("データベースに接続されていません");
        }
        return connection;
    }

    protected final void create() {
        path.getParent().toFile().mkdirs();
        try {
            path.toFile().createNewFile();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected final void connect() {
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

    protected final DatabaseTable.Builder tableBuilder(String name) {
        return new DatabaseTable.Builder(this, name);
    }

    protected final void disconnect() {
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

    public abstract void open();

    public abstract void close();
}
