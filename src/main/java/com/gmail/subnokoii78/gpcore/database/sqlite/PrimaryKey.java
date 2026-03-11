package com.gmail.subnokoii78.gpcore.database.sqlite;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class PrimaryKey {
    private final DataTable table;

    private final Map<String, ?> map;

    PrimaryKey(DataTable table, Map<String, ?> map) {
        this.table = table;
        this.map = map;

        for (final DataTable.Column<?> entry : table.getColumns()) {
            if (!entry.isKey()) continue;

            if (!map.containsKey(entry.getName())) {
                throw new SqliteDatabaseException("主キーのひとつ '" + entry.getName() + "' が不足しています");
            }
        }

        for (final Map.Entry<String, ?> entry : map.entrySet()) {
            if (table.getColumns().stream().noneMatch(e -> e.getName().equals(entry.getKey()) && e.isKey())) {
                throw new SqliteDatabaseException("'" + entry.getKey() + "' に一致するカラムが存在しないか、主キーではありません");
            }
        }
    }

    public DataTable getTable() {
        return table;
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

    protected <U> U use(String template, Function<PreparedStatement, U> callback) {
        final List<? extends Map.Entry<String, ?>> list = map.entrySet().stream().toList();

        final String sql = template.replace(
            "$key_values",
            String.join(
                " AND ",
                list.stream().map(entry -> String.format("%s=?", entry.getKey())).toList()
            )
        );

        try (final PreparedStatement preparedStatement = table.getDatabase().getConnection().prepareStatement(sql)) {
            for (int i = 0; i < list.size(); i++) {
                final Map.Entry<String, ?> entry = list.get(i);
                final Object value = entry.getValue();

                final DataType<?> type = table.getColumns().stream()
                    .filter(e -> e.getName().equals(entry.getKey()))
                    .findFirst().orElseThrow(() -> new SqliteDatabaseException("NEVER HAPPENS")).getType();

                switch (value) {
                    case Integer integerValue -> preparedStatement.setInt(i, integerValue);
                    case Double doubleValue -> preparedStatement.setDouble(i, doubleValue);
                    case String stringValue -> preparedStatement.setString(i, stringValue);
                    case byte[] bytesValue -> {
                        final Blob blob = table.getDatabase().getConnection().createBlob();
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
