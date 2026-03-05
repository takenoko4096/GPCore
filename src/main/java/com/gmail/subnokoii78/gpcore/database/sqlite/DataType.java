package com.gmail.subnokoii78.gpcore.database.sqlite;

import org.jspecify.annotations.NullMarked;

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
}
