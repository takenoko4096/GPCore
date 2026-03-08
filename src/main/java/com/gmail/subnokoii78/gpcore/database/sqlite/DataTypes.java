package com.gmail.subnokoii78.gpcore.database.sqlite;

import org.jspecify.annotations.NullMarked;

import java.sql.Types;

@NullMarked
public class DataTypes<T> {
    private final String identifier;

    private final int sqlType;

    private final Class<T> clazz;

    private DataTypes(String identifier, Class<T> clazz, int sqlType) {
        this.identifier = identifier;
        this.sqlType = sqlType;
        this.clazz = clazz;
    }

    public DataType<T> nullable() {
        return new DataType<>(identifier, clazz, sqlType, false);
    }

    public DataType<T> notNull() {
        return new DataType<>(identifier, clazz, sqlType, true);
    }

    public static final DataTypes<Integer> INTEGER = new DataTypes<>("INTEGER", Integer.class, Types.INTEGER);

    public static final DataTypes<Double> DOUBLE = new DataTypes<>("REAL", Double.class, Types.REAL);

    public static final DataTypes<String> STRING = new DataTypes<>("TEXT", String.class, Types.VARCHAR);

    public static final DataTypes<?> BYTE_ARRAY = new DataTypes<>("BLOB", byte[].class, Types.BLOB);
}
