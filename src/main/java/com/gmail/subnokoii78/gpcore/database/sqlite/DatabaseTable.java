package com.gmail.subnokoii78.gpcore.database.sqlite;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DatabaseTable {
    private final SqliteDatabase database;

    private final String name;

    private final Set<Entry<?>> entries;

    public DatabaseTable(SqliteDatabase database, String name, Set<Entry<?>> entries) {
        this.database = database;
        this.name = name;
        this.entries = entries;
    }

    @Override
    public int hashCode() {
        return Objects.hash(database, name, entries);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DatabaseTable that)) return false;
        return Objects.equals(database, that.database) && Objects.equals(name, that.name) && Objects.equals(entries, that.entries);
    }

    private String getIdentifier() {
        return name + '_' + Integer.toUnsignedString(hashCode());
    }

    public String getName() {
        return name;
    }

    public void create() {
        final String sql = String.format(
            """
            CREATE TABLE IF NOT EXISTS %s (
                %s,
                PRIMARY KEY (%s)
            );
            """,
            getIdentifier(),
            String.join(", ", entries.stream().map(Entry::toTypedName).toList()),
            String.join(", ", entries.stream().filter(Entry::isKey).map(Entry::getName).toList())
        );

        try (final Statement statement = database.getConnection().createStatement()) {
            statement.execute(sql);
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("テーブルの作成に失敗しました: ", e);
        }
    }

    public void delete() {
        final String sql = String.format(
            """
            DELETE FROM %s
            """,
            getIdentifier()
        );

        try (final Statement statement = database.getConnection().createStatement()) {
            statement.execute(sql);
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("データベースのアクセスに問題が発生しました", e);
        }
    }

    public <T> boolean has(Map<String, T> key) {
        for (final Map.Entry<String, T> entry : key.entrySet()) {
            if (entries.stream().anyMatch(e -> !(e.name.equals(entry.getKey()) && e.isKey))) {
                throw new SqliteDatabaseException("一致するエントリが存在しないか、主キーではありません");
            }
        }

        final String sql = String.format(
            """
            SELECT 1 FROM %s
            WHERE %s
            """,
            getIdentifier(),
            String.join(" AND ", String.format("%s=?", name))
        );

        try (final PreparedStatement preparedStatement = database.getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, position.x());
            preparedStatement.setInt(2, position.y());
            preparedStatement.setInt(3, position.z());

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
        }
    }

    public static final class Entry<T> {
        private final String name;

        private final DataType<T> type;

        private final boolean isKey;

        public Entry(String name, DataType<T> type, boolean isKey) {
            this.name = name;
            this.type = type;
            this.isKey = isKey;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type, isKey);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry<?> entry)) return false;
            return isKey == entry.isKey && Objects.equals(name, entry.name) && Objects.equals(type, entry.type);
        }

        public String getName() {
            return name;
        }

        public DataType<T> getType() {
            return type;
        }

        public boolean isKey() {
            return isKey;
        }

        private String toTypedName() {
            return name + ' ' + type;
        }
    }
}
