package com.gmail.subnokoii78.gpcore.database.sqlite;

import org.jspecify.annotations.NullMarked;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

@NullMarked
public class DatabaseTable {
    private final SqliteDatabase database;

    private final String name;

    private final Set<Entry<?>> entries;

    protected DatabaseTable(SqliteDatabase database, String name, Set<Entry<?>> entries) {
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

    public boolean create() {
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
            return statement.execute(sql);
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("テーブルの作成に失敗しました: ", e);
        }
    }

    public boolean delete() {
        final String sql = String.format(
            """
            DELETE FROM %s
            """,
            getIdentifier()
        );

        try (final Statement statement = database.getConnection().createStatement()) {
            return statement.execute(sql);
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("データベースのアクセスに問題が発生しました", e);
        }
    }

    private <U> U useKey(String template, Map<String, ?> key, Function<PreparedStatement, U> callback) {
        for (final Map.Entry<String, ?> entry : key.entrySet()) {
            if (entries.stream().anyMatch(e -> !(e.name.equals(entry.getKey()) && e.isKey))) {
                throw new SqliteDatabaseException("一致するエントリが存在しないか、主キーではありません");
            }
        }

        final List<? extends Map.Entry<String, ?>> list = key.entrySet().stream().toList();

        final String sql = template.replace(
            "$key_values",
            String.join(
                " AND ",
                list.stream().map(entry -> String.format("%s=?", entry.getKey())).toList()
            )
        );

        try (final PreparedStatement preparedStatement = database.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < list.size(); i++) {
                final Object value = list.get(i).getValue();

                switch (value) {
                    case Integer integerValue -> preparedStatement.setInt(i, integerValue);
                    case Double doubleValue -> preparedStatement.setDouble(i, doubleValue);
                    case String stringValue -> preparedStatement.setString(i, stringValue);
                    default -> throw new SqliteDatabaseException("not implemented: " + value.getClass().getName());
                }
            }

            return callback.apply(preparedStatement);
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
        }
    }

    public boolean contains(Map<String, ?> key) {
        final String sql = String.format(
            """
            SELECT 1 FROM %s
            WHERE $key_values
            """,
            getIdentifier()
        );

        return useKey(sql, key, preparedStatement -> {
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
            catch (SQLException e) {
                throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
            }
        });
    }

    /*public Map<String, ?> get(Map<String, ?> key) {
        final String sql = String.format(
            """
            SELECT * FROM %s
            WHERE $key_values
            """,
            getIdentifier()
        );

        final Map<String, ?> map = new HashMap<>();

        return useKey(sql, key, preparedStatement -> {
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                }
            }
            catch (SQLException e) {
                throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
            }
        });
    }*/

    public static <T> Entry<T> entry(String name, DataType<T> type, boolean isKey) {
        return new Entry<>(name, type, isKey);
    }

    public static final class Entry<T> {
        private final String name;

        private final DataType<T> type;

        private final boolean isKey;

        private Entry(String name, DataType<T> type, boolean isKey) {
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
