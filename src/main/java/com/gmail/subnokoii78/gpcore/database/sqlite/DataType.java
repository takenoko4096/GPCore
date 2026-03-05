package com.gmail.subnokoii78.gpcore.database.sqlite;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class DataType<T> {
    private final String name;

    private final Class<T> clazz;

    protected DataType(String name, Class<T> clazz, boolean notNull) {
        this.name = name + (notNull ? (' ' + "NOT NULL") : (""));
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public Class<T> getClazz() {
        return clazz;
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
