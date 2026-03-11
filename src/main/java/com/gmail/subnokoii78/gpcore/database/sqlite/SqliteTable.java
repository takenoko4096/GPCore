package com.gmail.subnokoii78.gpcore.database.sqlite;

import org.jspecify.annotations.NullMarked;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

@NullMarked
public final class SqliteTable {
    private final SqliteDatabase database;

    private final String name;

    private final List<TableDefinitionEntry<?>> entries;

    SqliteTable(SqliteDatabase database, String name, List<TableDefinitionEntry<?>> entries) {
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
        if (!(o instanceof SqliteTable that)) return false;
        return Objects.equals(database, that.database) && Objects.equals(name, that.name) && Objects.equals(entries, that.entries);
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
            name,
            String.join(", ", entries.stream().map(TableDefinitionEntry::toTypedName).toList()),
            String.join(", ", entries.stream().filter(TableDefinitionEntry::isKey).map(TableDefinitionEntry::getName).toList())
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
                    for (final TableDefinitionEntry<?> entry : entries) {
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

                for (final TableDefinitionEntry<?> entry : this.entries) {
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

    public static final class TableDefinitionEntry<T> {
        private final String name;

        private final DataType<T> type;

        private final boolean isKey;

        private TableDefinitionEntry(String name, DataType<T> type, boolean isKey) {
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
            if (!(o instanceof TableDefinitionEntry<?> entry)) return false;
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

        private final List<TableDefinitionEntry<?>> entries = new ArrayList<>();

        Builder(SqliteDatabase database, String name) {
            this.database = database;
            this.name = name;
        }

        public <T> Builder primaryKey(String name, DataType<T> type) {
            if (entries.stream().anyMatch(e -> e.name.equals(name))) {
                throw new SqliteDatabaseException("カラムが重複しています: " + name);
            }
            entries.add(new TableDefinitionEntry<>(name, type, true));
            return this;
        }

        public <T> Builder normalColumn(String name, DataType<T> type) {
            if (entries.stream().anyMatch(e -> e.name.equals(name))) {
                throw new SqliteDatabaseException("カラムが重複しています: " + name);
            }
            entries.add(new TableDefinitionEntry<>(name, type, false));
            return this;
        }

        SqliteTable toTable() {
            return new SqliteTable(database, name, entries);
        }
    }

    public static class PrimaryKey {
        private final SqliteTable table;

        private final Map<String, ?> map;

        private PrimaryKey(SqliteTable table, Map<String, ?> map) {
            this.table = table;
            this.map = map;

            for (final TableDefinitionEntry<?> entry : table.entries) {
                if (!entry.isKey) continue;

                if (!map.containsKey(entry.name)) {
                    throw new SqliteDatabaseException("主キーのひとつ '" + entry.name + "' が不足しています");
                }
            }

            for (final Map.Entry<String, ?> entry : map.entrySet()) {
                if (table.entries.stream().noneMatch(e -> e.name.equals(entry.getKey()) && e.isKey)) {
                    throw new SqliteDatabaseException("'" + entry.getKey() + "' に一致するカラムが存在しないか、主キーではありません");
                }
            }
        }

        public Map<String, ?> getValues() {
            return map;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof PrimaryKey that)) return false;
            return Objects.equals(table, that.table) && Objects.equals(map, that.map);
        }

        @Override
        public int hashCode() {
            return Objects.hash(table, map);
        }

        private <U> U use(String template, Function<PreparedStatement, U> callback) {
            final List<? extends Map.Entry<String, ?>> list = map.entrySet().stream().toList();

            final String sql = template.replace(
                "$key_values",
                String.join(
                    " AND ",
                    list.stream().map(entry -> String.format("%s=?", entry.getKey())).toList()
                )
            );

            try (final PreparedStatement preparedStatement = table.database.getConnection().prepareStatement(sql)) {
                for (int i = 0; i < list.size(); i++) {
                    final Map.Entry<String, ?> entry = list.get(i);
                    final Object value = entry.getValue();

                    final DataType<?> type = table.entries.stream()
                        .filter(e -> e.name.equals(entry.getKey()))
                        .findFirst().orElseThrow(() -> new SqliteDatabaseException("NEVER HAPPENS")).type;

                    switch (value) {
                        case Integer integerValue -> preparedStatement.setInt(i, integerValue);
                        case Double doubleValue -> preparedStatement.setDouble(i, doubleValue);
                        case String stringValue -> preparedStatement.setString(i, stringValue);
                        case byte[] bytesValue -> {
                            final Blob blob = table.database.getConnection().createBlob();
                            blob.setBytes(1, bytesValue);
                            preparedStatement.setBlob(i, blob);
                        }
                        case null -> preparedStatement.setNull(i, type.toSqlType());
                        default -> throw new SqliteDatabaseException("not implemented: " + value.getClass().getName());
                    }
                }

                return callback.apply(preparedStatement);
            }
            catch (SQLException e) {
                throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
            }
        }

        public DataRecord createRecord(Map<String, ?> data) {
            return new DataRecord(this, data);
        }
    }

    public static class DataRecord {
        private final PrimaryKey primaryKey;

        private final Map<String, ?> values;

        public DataRecord(PrimaryKey primaryKey, Map<String, ?> values) {
            this.primaryKey = primaryKey;
            for (final String k : primaryKey.map.keySet()) {
                if (values.containsKey(k)) {
                    throw new SqliteDatabaseException("values に主キー '" + k + "' が含まれています: values=" + values);
                }
            }

            for (final TableDefinitionEntry<?> entry : primaryKey.table.entries) {
                if (entry.isKey) continue;

                if (!values.containsKey(entry.name)) {
                    throw new SqliteDatabaseException("values にカラム '" + entry.name + "' が不足しています");
                }
            }

            for (final Map.Entry<String, ?> entry : values.entrySet()) {
                if (primaryKey.table.entries.stream().noneMatch(e -> e.name.equals(entry.getKey()))) {
                    throw new SqliteDatabaseException("無効なカラムが含まれています: " + entry.getKey());
                }
            }

            this.values = values;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof DataRecord that)) return false;
            return Objects.equals(primaryKey, that.primaryKey) && Objects.equals(values, that.values);
        }

        @Override
        public int hashCode() {
            return Objects.hash(primaryKey, values);
        }

        public PrimaryKey getPrimaryKey() {
            return primaryKey;
        }

        public Map<String, ?> getValues() {
            return values;
        }

        public Map<String, ?> merged() {
            final Map<String, Object> map = new HashMap<>();

            map.putAll(primaryKey.map);
            map.putAll(values);

            return map;
        }
    }
}
