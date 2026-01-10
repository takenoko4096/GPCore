package com.gmail.subnokoii78.gpcore.vector;

import com.gmail.subnokoii78.gpcore.generic.TriFunction;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

/**
 * ヨー角・ピッチ角・ロール角による回転を表現するクラス
 */
@NullMarked
public final class TripleAxisRotationBuilder implements VectorBuilder<TripleAxisRotationBuilder, Float> {
    private float yaw, pitch, roll;

    public TripleAxisRotationBuilder(float yaw, float pitch, float roll) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public TripleAxisRotationBuilder() {
        this.yaw = 0;
        this.pitch = 0;
        this.roll = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripleAxisRotationBuilder that = (TripleAxisRotationBuilder) o;
        return Float.compare(yaw, that.yaw) == 0 && Float.compare(pitch, that.pitch) == 0 && Float.compare(roll, that.roll) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(yaw, pitch, roll);
    }

    @Override
    public boolean equals(TripleAxisRotationBuilder other) {
        return yaw == other.yaw
            && pitch == other.pitch
            && roll == other.roll;
    }

    public boolean similar(TripleAxisRotationBuilder other, int digits) {
        return format("($c, $c, $c)", digits).equals(other.format("($c, $c, $c)", digits));
    }

    public float yaw() {
        return yaw;
    }

    @Destructive
    public TripleAxisRotationBuilder yaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public float pitch() {
        return pitch;
    }

    @Destructive
    public TripleAxisRotationBuilder pitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public float roll() {
        return roll;
    }

    @Destructive
    public TripleAxisRotationBuilder roll(float roll) {
        this.roll = roll;
        return this;
    }

    @Override
    @Destructive
    public TripleAxisRotationBuilder calculate(UnaryOperator<Float> operator) {
        yaw = operator.apply(yaw);
        pitch = operator.apply(pitch);
        roll = operator.apply(roll);
        return this;
    }

    @Override
    @Destructive
    public TripleAxisRotationBuilder calculate(TripleAxisRotationBuilder other, BiFunction<Float, Float, Float> operator) {
        yaw = operator.apply(yaw, other.yaw);
        pitch = operator.apply(pitch, other.pitch);
        roll = operator.apply(roll, other.roll);
        return this;
    }

    @Override
    @Destructive
    public TripleAxisRotationBuilder calculate(TripleAxisRotationBuilder other1, TripleAxisRotationBuilder other2, TriFunction<Float, Float, Float, Float> operator) {
        this.yaw = operator.apply(yaw, other1.yaw, other2.yaw);
        this.pitch = operator.apply(pitch, other1.pitch, other2.pitch);
        this.roll = operator.apply(roll, other1.roll, other2.roll);
        return this;
    }

    @Override
    @Destructive
    public TripleAxisRotationBuilder add(TripleAxisRotationBuilder other) {
        calculate(other, Float::sum);
        return this;
    }

    @Override
    @Destructive
    public TripleAxisRotationBuilder subtract(TripleAxisRotationBuilder other) {
        return calculate(other, (a, b) -> a - b);
    }

    @Override
    @Destructive
    public TripleAxisRotationBuilder scale(Float scalar) {
        return calculate(component -> component * scalar);
    }

    @Override
    public TripleAxisRotationBuilder invert() {
        return getObjectsCoordsSystem().back();
    }

    @Override
    @Destructive
    public TripleAxisRotationBuilder clamp(TripleAxisRotationBuilder min, TripleAxisRotationBuilder max) {
        return calculate(min, max, (value, minValue, maxValue) -> Math.max(minValue, Math.min(value, maxValue)));
    }

    public String format(String format, int digits) {
        final String floatFormat = "%." + digits + "f";

        final String yaw = String.format(floatFormat, this.yaw);
        final String pitch = String.format(floatFormat, this.pitch);
        final String roll = String.format(floatFormat, this.roll);

        return format
            .replaceAll("\\$x", yaw)
            .replaceAll("\\$y", pitch)
            .replaceAll("\\$z", roll)
            .replaceFirst("\\$c", yaw)
            .replaceFirst("\\$c", pitch)
            .replaceFirst("\\$c", roll)
            .replaceAll("\\$c", "");
    }

    @Override
    public String toString() {
        return format("($x, $y, $z)", 2);
    }

    @Override
    public TripleAxisRotationBuilder copy() {
        return new TripleAxisRotationBuilder(yaw, pitch, roll);
    }

    @Override
    public boolean isZero() {
        return equals(new TripleAxisRotationBuilder());
    }

    public ObjectCoordsSystem getObjectsCoordsSystem() {
        return new ObjectCoordsSystem(this);
    }

    public DualAxisRotationBuilder toRotation2f() {
        return new DualAxisRotationBuilder(yaw, pitch);
    }

    public Vector3Builder getDirection3d() {
        return toRotation2f().getDirection3d();
    }

    public Quaternionf getQuaternion4f() {
        final var quaternion = new Quaternionf(0f, 0f, 0f, 1f);
        final var axes = new DualAxisRotationBuilder(yaw, pitch).getDirection3d().getLocalAxisProvider();

        final BiConsumer<Vector3Builder, Float> function = ((axis, angle) -> {
            final Vector3Builder normalized = axis.copy().normalize();
            quaternion.rotateAxis(
                (float) (angle * Math.PI / 180),
                (float) normalized.x(),
                (float) normalized.y(),
                (float) normalized.z()
            );
        });

        function.accept(axes.getZ(), roll);
        function.accept(axes.getX(), pitch);
        function.accept(new Vector3Builder(0, 1, 0), -(yaw + 90));
        return quaternion;
    }

    public static TripleAxisRotationBuilder from(DualAxisRotationBuilder other) {
        return new TripleAxisRotationBuilder(other.yaw(), other.pitch(), 0);
    }

    public static final class ObjectCoordsSystem {
        private final TripleAxisRotationBuilder rotation;

        private ObjectCoordsSystem(TripleAxisRotationBuilder rotation) {
            this.rotation = rotation.copy();
        }

        public Vector3Builder getX() {
            final Vector3Builder forward = getZ();

            return new Vector3Builder(forward.z(), 0, -forward.x())
                .normalize()
                .rotate(forward, rotation.roll);
        }

        public Vector3Builder getY() {
            return this.getZ().cross(this.getX());
        }

        public Vector3Builder getZ() {
            return rotation.getDirection3d();
        }

        public TripleAxisRotationBuilder forward() {
            return rotation.copy();
        }

        public TripleAxisRotationBuilder back() {
            return ofAxes(
                getX().invert(),
                getY()
            );
        }

        public TripleAxisRotationBuilder left() {
            return ofAxes(
                getZ().invert(),
                getY()
            );
        }

        public TripleAxisRotationBuilder right() {
            return ofAxes(
                getZ(),
                getY()
            );
        }

        public TripleAxisRotationBuilder up() {
            return ofAxes(
                getX(),
                getZ().invert()
            );
        }

        public TripleAxisRotationBuilder down() {
            return ofAxes(
                getX(),
                getZ()
            );
        }

        private TripleAxisRotationBuilder ofAxes(Vector3Builder x, Vector3Builder y) {
            final Vector3Builder z = x.cross(y);

            return new TripleAxisRotationBuilder(
                (float) (Math.atan2(-z.x(), z.z()) * 180 / Math.PI),
                (float) (Math.asin(-z.y()) * 180 / Math.PI),
                (float) (Math.atan2(x.y(), y.y()) * 180 / Math.PI)
            );
        }
    }
}
