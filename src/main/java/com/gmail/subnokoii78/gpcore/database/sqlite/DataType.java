package com.gmail.subnokoii78.gpcore.database.sqlite;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public final class DataType<T> {
    private final String name;

    private final Class<T> clazz;

    private final int sqlType;

    DataType(String name, Class<T> clazz, int sqlType, boolean notNull) {
        this.name = name + (notNull ? (' ' + "NOT NULL") : (""));
        this.sqlType = sqlType;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public int toSqlType() {
        return sqlType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, clazz.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DataType<?> dataType)) return false;
        return Objects.equals(name, dataType.name) && Objects.equals(clazz, dataType.clazz);
    }
}
