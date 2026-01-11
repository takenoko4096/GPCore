package com.gmail.subnokoii78.gpcore.random;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class NumberRange<T extends Number> {
    private final T min;

    private final T max;

    private NumberRange(T value1, T value2) {
        if (value1.doubleValue() >= value2.doubleValue()) {
            min = value2;
            max = value1;
        }
        else {
            min = value1;
            max = value2;
        }
    }

    public T min() {
        return min;
    }

    public T max() {
        return max;
    }

    public boolean within(byte value) {
        return min.byteValue() <= value && value <= max.byteValue();
    }

    public boolean within(short value) {
        return min.shortValue() <= value && value <= max.shortValue();
    }

    public boolean within(int value) {
        return min.intValue() <= value && value <= max.intValue();
    }

    public boolean within(long value) {
        return min.longValue() <= value && value <= max.longValue();
    }

    public boolean within(float value) {
        return min.floatValue() <= value && value <= max.floatValue();
    }

    public boolean within(double value) {
        return min.doubleValue() <= value && value <= max.doubleValue();
    }

    public byte clamp(byte value) {
        if (value < min.byteValue()) {
            return min.byteValue();
        }
        else if (value > max.byteValue()) {
            return max.byteValue();
        }
        else return value;
    }

    public short clamp(short value) {
        if (value < min.shortValue()) {
            return min.shortValue();
        }
        else if (value > max.shortValue()) {
            return max.shortValue();
        }
        else return value;
    }

    public boolean isFinite() {
        return !(Double.isFinite(min.doubleValue()) || Double.isFinite(max.doubleValue()));
    }

    public int clamp(int value) {
        if (value < min.intValue()) {
            return min.intValue();
        }
        else if (value > max.intValue()) {
            return max.intValue();
        }
        else return value;
    }

    public long clamp(long value) {
        if (value < min.longValue()) {
            return min.longValue();
        }
        else if (value > max.longValue()) {
            return max.longValue();
        }
        else return value;
    }

    public float clamp(float value) {
        if (value < min.floatValue()) {
            return min.floatValue();
        }
        else if (value > max.floatValue()) {
            return max.floatValue();
        }
        else return value;
    }

    public double clamp(double value) {
        if (value < min.doubleValue()) {
            return min.doubleValue();
        }
        else if (value > max.doubleValue()) {
            return max.doubleValue();
        }
        else return value;
    }

    public byte[] bytes() {
        final byte max = this.max.byteValue();
        final byte min = this.min.byteValue();

        final byte[] array = new byte[max - min + 1];

        for (byte i = min; i <= max; i++) {
            array[i] = i;
        }

        return array;
    }

    public short[] shorts() {
        final short max = this.max.shortValue();
        final short min = this.min.shortValue();

        final short[] array = new short[max - min + 1];

        for (short i = min; i <= max; i++) {
            array[i] = i;
        }

        return array;
    }

    public int[] ints() {
        final int max = this.max.intValue();
        final int min = this.min.intValue();

        final int[] array = new int[max - min + 1];

        for (int i = min; i <= max; i++) {
            array[i] = i;
        }

        return array;
    }

    public static <T extends Number> NumberRange<T> of(T value1, T value2) {
        return new NumberRange<>(value1, value2);
    }

    public static final NumberRange<Integer> INT32 = NumberRange.of(-2147483648, 2147483647);

    public static final NumberRange<Long> UINT32 = NumberRange.of(0L, 4294967295L);
}
