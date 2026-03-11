package com.gmail.subnokoii78.gpcore.database.sqlite;

import org.jspecify.annotations.NullMarked;

import java.sql.*;
import java.util.*;

@NullMarked
public final class DataTable {
    private final SqliteDatabase database;

    private final String name;

    private final List<Column<?>> columns;

    DataTable(SqliteDatabase database, String name, List<Column<?>> columns) {
        this.database = database;
        this.name = name;
        this.columns = columns;
    }

    @Override
    public int hashCode() {
        return Objects.hash(database, name, columns);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DataTable that)) return false;
        return Objects.equals(database, that.database) && Objects.equals(name, that.name) && Objects.equals(columns, that.columns);
    }

    public String getName() {
        return name;
    }

    public SqliteDatabase getDatabase() {
        return database;
    }

    public List<Column<?>> getColumns() {
        return columns;
    }

    public boolean create() {
        final String sql = String.format(
            """
            CREATE TABLE IF NOT EXISTS %s (
                %s,
                PRIMARY KEY (%s)
            );
            """,
            name,
            String.join(", ", columns.stream().map(Column::toTypedName).toList()),
            String.join(", ", columns.stream().filter(Column::isKey).map(Column::getName).toList())
        );

        try (final Statement statement = database.getConnection().createStatement()) {
            return statement.execute(sql);
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("テーブルの作成に失敗しました: ", e);
        }
    }

    public boolean contains(PrimaryKey key) {
        final String sql = String.format(
            """
            SELECT 1 FROM %s
            WHERE $key_values
            """,
            name
        );

        return key.use(sql, preparedStatement -> {
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
            catch (SQLException e) {
                throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
            }
        });
    }

    public DataRecord get(PrimaryKey key) {
        final String sql = String.format(
            """
            SELECT * FROM %s
            WHERE $key_values
            """,
            name
        );

        final Map<String, Object> map = new HashMap<>();

        key.use(sql, preparedStatement -> {
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    for (final Column<?> entry : columns) {
                        if (entry.isKey) continue;
                        map.put(entry.name, resultSet.getObject(entry.name));
                    }
                }
            }
            catch (SQLException e) {
                throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
            }

            return 0;
        });

        return key.createRecord(map);
    }

    public Set<DataRecord> values() {
        final String sql = String.format(
            """
            SELECT * FROM %s
            """,
            name
        );

        final Set<DataRecord> entries = new HashSet<>();

        try (final Statement statement = database.getConnection().createStatement()) {
            final ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                final Map<String, Object> map = new HashMap<>();
                final Map<String, Object> keyMap = new HashMap<>();

                for (final Column<?> entry : this.columns) {
                    if (entry.isKey) {
                        keyMap.put(entry.name, resultSet.getObject(entry.name));
                    }
                    else {
                        map.put(entry.name, resultSet.getObject(entry.name));
                    }
                }

                final PrimaryKey primaryKey = new PrimaryKey(this, keyMap);
                entries.add(primaryKey.createRecord(map));
            }
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("データベースのアクセスに問題が発生しました", e);
        }

        return entries;
    }

    public boolean insert(DataRecord dataRecord) {
        final String sql = String.format(
            """
            INSERT OR IGNORE INTO %s(%s) VALUES (%s)
            """,
            name,
            String.join(", ", dataRecord.merged().keySet()),
            String.join(", ", dataRecord.merged().values().stream().map(v -> {
                if (v instanceof String s) {
                    return "'" + s + "'";
                }
                else return v.toString();
            }).toList())
        );

        try (final Statement statement = database.getConnection().createStatement()) {
            return statement.execute(sql);
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("データベースのアクセスに問題が発生しました", e);
        }
    }

    public boolean delete(PrimaryKey key) {
        final String sql = String.format(
            """
            DELETE FROM %s
            WHERE $key_values
            """,
            name
        );

        return key.use(sql, preparedStatement -> {
            try {
                return preparedStatement.execute();
            }
            catch (SQLException e) {
                throw new SqliteDatabaseException("データベースのアクセスに問題が発生しました", e);
            }
        });
    }

    public boolean clear() {
        final String sql = String.format(
            """
            DELETE FROM %s
            """,
            name
        );

        try (final Statement statement = database.getConnection().createStatement()) {
            return statement.execute(sql);
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("データベースのアクセスに問題が発生しました", e);
        }
    }

    public boolean delete() {
        final String sql = String.format(
            """
            DROP TABLE %s;
            """,
            name
        );

        try (final Statement statement = database.getConnection().createStatement()) {
            return statement.execute(sql);
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("データベースのアクセスに問題が発生しました", e);
        }
    }

    public PrimaryKey createPrimaryKey(Map<String, ?> values) {
        return new PrimaryKey(this, values);
    }

    public static final class Column<T> {
        private final String name;

        private final DataType<T> type;

        private final boolean isKey;

        private Column(String name, DataType<T> type, boolean isKey) {
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
            if (!(o instanceof Column<?> entry)) return false;
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
            return name + ' ' + type.getName();
        }
    }

    public static final class Builder {
        private final SqliteDatabase database;

        private final String name;

        private final List<Column<?>> entries = new ArrayList<>();

        Builder(SqliteDatabase database, String name) {
            this.database = database;
            this.name = name;
        }

        public <T> Builder primaryKey(String name, DataType<T> type) {
            if (entries.stream().anyMatch(e -> e.name.equals(name))) {
                throw new SqliteDatabaseException("カラムが重複しています: " + name);
            }
            entries.add(new Column<>(name, type, true));
            return this;
        }

        public <T> Builder normalColumn(String name, DataType<T> type) {
            if (entries.stream().anyMatch(e -> e.name.equals(name))) {
                throw new SqliteDatabaseException("カラムが重複しています: " + name);
            }
            entries.add(new Column<>(name, type, false));
            return this;
        }

        DataTable toTable() {
            return new DataTable(database, name, entries);
        }
    }

}
