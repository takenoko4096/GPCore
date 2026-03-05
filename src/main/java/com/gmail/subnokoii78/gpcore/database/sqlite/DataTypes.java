package com.gmail.subnokoii78.gpcore.database.sqlite;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class DataTypes {
    private DataTypes() {}

    public static final DataType<Integer> INTEGER_NULLABLE = new DataType<>("INTEGER", Integer.class, false);

    public static final DataType<String> STRING_NULLABLE = new DataType<>("TEXT", String.class, false);

    public static final DataType<Double> DOUBLE_NULLABLE = new DataType<>("REAL", Double.class, false);

    public static final DataType<Integer> INTEGER_NOTNULL = new DataType<>("INTEGER", Integer.class, true);

    public static final DataType<String> STRING_NOTNULL = new DataType<>("TEXT", String.class, true);

    public static final DataType<Double> DOUBLE_NOTNULL = new DataType<>("REAL", Double.class, true);
}
