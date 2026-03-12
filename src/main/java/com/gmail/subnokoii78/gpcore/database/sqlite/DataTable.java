package com.gmail.subnokoii78.gpcore.database.sqlite;

import org.jspecify.annotations.NullMarked;

import java.sql.*;
import java.util.*;

@NullMarked
public final class DataTable {
    private final SqliteDatabase database;

    private final String name;

    private final List<Column<?>> columns;

    private final Queue<DataRecord> batches = new PriorityQueue<>();

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
            $where
            """,
            name
        );

        return key.query(sql, preparedStatement -> {
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
            $where
            """,
            name
        );

        final Map<String, Object> map = new HashMap<>();

        key.query(sql, preparedStatement -> {
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
        final List<Column<?>> list = columns.stream().toList();

        final String sql = String.format(
            """
            INSERT OR REPLACE INTO %s(%s) VALUES (%s)
            """,
            name,
            String.join(", ", list.stream().map(Column::getName).toList()),
            String.join(", ", list.stream().map(c -> "?").toList())
        );

        final Map<String, ?> merged = dataRecord.merged();

        try (final PreparedStatement preparedStatement = database.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < list.size(); i++) {
                final Column<?> column = list.get(i);
                final Object value = merged.get(column.getName());

                preparedStatement.setObject(i, value);
            }

            return preparedStatement.execute();
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("データベースのアクセスに問題が発生しました", e);
        }
    }

    public void batch(DataRecord dataRecord) {
        batches.add(dataRecord);
    }

    public int flushByBulkInsert(int limit) {
        final List<Column<?>> columnList = columns.stream().toList();

        final StringBuilder sql = new StringBuilder(String.format(
            """
            INSERT OR REPLACE INTO %s(%s) VALUES
            """,
            name,
            String.join(", ", columnList.stream().map(Column::getName).toList())
        ));

        final int recordCount = Math.min(batches.size(), limit);
        final String recordWithPlaceHolder = '(' + String.join(", ", columnList.stream().map(c -> "?").toList()) + ')';

        for (int i = 0; i < recordCount; i++) {
            if (i > 0) sql.append(", ");
            sql.append(recordWithPlaceHolder);
        }

        try (final PreparedStatement preparedStatement = database.getConnection().prepareStatement(sql.toString())) {

            int i = 1;
            int j = 0;
            while (j < recordCount) {
                final DataRecord record = batches.poll();

                if (record == null) break; // maybe never happens

                final Map<String, ?> merged = record.merged();

                for (final Column<?> column : columnList) {
                    final Object value = merged.get(column.getName());
                    preparedStatement.setObject(i++, value);
                }

                j++;
            }

            return preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースのアクセスに問題が発生しました", e);
        }
    }

    public int flushByTransaction(int limit) {
        final List<Column<?>> columnList = columns.stream().toList();

        final String sql = String.format(
            """
            INSERT OR REPLACE INTO %s(%s) VALUES (%s)
            """,
            name,
            String.join(", ", columnList.stream().map(Column::getName).toList()),
            String.join(", ", columnList.stream().map(c -> "?").toList())
        );

        final int recordCount = Math.min(batches.size(), limit);

        try (final PreparedStatement preparedStatement = database.getConnection().prepareStatement(sql)) {
            int i = 0;
            while (i < recordCount) {
                final DataRecord dataRecord = batches.poll();

                if (dataRecord == null) break; // maybe never happens

                final Map<String, ?> merged = dataRecord.merged();

                for (int j = 0; j < columnList.size(); j++) {
                    final Column<?> column = columnList.get(j);
                    preparedStatement.setObject(j, merged.get(column.getName()));
                }

                preparedStatement.addBatch();

                i++;
            }

            return preparedStatement.executeBatch().length;
        }
        catch (SQLException e) {
            throw new SqliteDatabaseException("データベースのアクセスに問題が発生しました", e);
        }
    }

    public boolean delete(PrimaryKey key) {
        final String sql = String.format(
            """
            DELETE FROM %s
            $where
            """,
            name
        );

        return key.query(sql, preparedStatement -> {
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
