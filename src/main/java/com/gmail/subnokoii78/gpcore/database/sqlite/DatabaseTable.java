package com.gmail.subnokoii78.gpcore.database.sqlite;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

public class DatabaseTable {
    private final SqliteDatabase database;

    private final String name;

    private final Set<Key<?>> keys;

    public DatabaseTable(SqliteDatabase database, String name, Set<Key<?>> keys) {
        this.database = database;
        this.name = name;
        this.keys = keys;
    }

    public void create() {
        final StringBuilder keysSql = new StringBuilder();
        for (final Key<?> key : keys) {
            keysSql.append(String.format(
                "%s %s,\n",
                key.getName(),
                key.getType()
            ));
        }

        final String sql = String.format(
            """
            CREATE TABLE IF NOT EXISTS %s (
                %s
                PRIMARY KEY (%s)
            );
            """,
            name,
            keysSql,
            String.join(", ", keys.stream().filter(Key::isPrimary).map(Key::getName).toArray(String[]::new))
        );

        try (final Statement statement = database.getConnection().createStatement()) {
            statement.execute(sql);
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("テーブルの作成に失敗しました: ", e);
        }
    }

    public static final class Key<T> {
        private final String name;

        private final DataType<T> type;

        private final boolean isPrimary;

        public Key(String name, DataType<T> type, boolean isPrimary) {
            this.name = name;
            this.type = type;
            this.isPrimary = isPrimary;
        }

        public String getName() {
            return name;
        }

        public DataType<T> getType() {
            return type;
        }

        public boolean isPrimary() {
            return isPrimary;
        }
    }
}
