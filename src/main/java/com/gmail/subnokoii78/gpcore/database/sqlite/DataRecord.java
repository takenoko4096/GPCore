package com.gmail.subnokoii78.gpcore.database.sqlite;

import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NullMarked
public class DataRecord {
    private final PrimaryKey primaryKey;

    private final Map<String, ?> values;

    protected DataRecord(PrimaryKey primaryKey, Map<String, ?> values) {
        this.primaryKey = primaryKey;
        for (final String k : primaryKey.getValues().keySet()) {
            if (values.containsKey(k)) {
                throw new SqliteDatabaseException("values に主キー '" + k + "' が含まれています: values=" + values);
            }
        }

        for (final DataTable.Column<?> entry : primaryKey.getTable().getColumns()) {
            if (entry.isKey()) continue;

            if (!values.containsKey(entry.getName())) {
                throw new SqliteDatabaseException("values にカラム '" + entry.getName() + "' が不足しています");
            }
        }

        for (final Map.Entry<String, ?> entry : values.entrySet()) {
            if (primaryKey.getTable().getColumns().stream().noneMatch(e -> e.getName().equals(entry.getKey()))) {
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

        map.putAll(primaryKey.getValues());
        map.putAll(values);

        return map;
    }
}
