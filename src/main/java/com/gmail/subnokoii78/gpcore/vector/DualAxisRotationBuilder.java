package com.gmail.subnokoii78.gpcore.vector;

import com.gmail.subnokoii78.gpcore.generic.TriFunction;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

/**
 * ヨー角・ピッチ角による回転を表現するクラス
 */
@NullMarked
public class DualAxisRotationBuilder implements VectorBuilder<DualAxisRotationBuilder, Float> {
    private float yaw, pitch;

    /**
     * 三次元零回転を作成します。
     */
    public DualAxisRotationBuilder() {}

    /**
     * 三次元回転を作成します。
     * @param yaw X成分
     * @param pitch Y成分
     */
    public DualAxisRotationBuilder(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public List<Float> components() {
        return List.of(yaw, pitch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DualAxisRotationBuilder that = (DualAxisRotationBuilder) o;
        return Float.compare(yaw, that.yaw) == 0 && Float.compare(pitch, that.pitch) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(yaw, pitch);
    }

    @Override
    public boolean equals(DualAxisRotationBuilder other) {
        return yaw == other.yaw
            && pitch == other.pitch;
    }

    public boolean similar(DualAxisRotationBuilder other, int digits) {
        return format("($c, $c)", digits).equals(other.format("($c, $c)", digits));
    }

    /**
     * この回転のX成分(横回転)の値を返します。
     * @return X成分の値
     */
    public float yaw() {
        return yaw;
    }

    /**
     * この回転のX成分(横回転)の値を変更します。
     * @param value 新しい値
     * @return this
     */
    @Destructive
    public DualAxisRotationBuilder yaw(float value) {
        yaw = value;
        return this;
    }

    /**
     * この回転のY成分(縦回転)の値を返します。
     * @return Y成分の値
     */
    public float pitch() {
        return pitch;
    }

    /**
     * この回転のY成分(縦回転)の値を変更します。
     * @param value 新しい値
     * @return this
     */
    @Destructive
    public DualAxisRotationBuilder pitch(float value) {
        pitch = value;
        return this;
    }

    @Override
    @Destructive
    public DualAxisRotationBuilder calculate(UnaryOperator<Float> operator) {
        yaw = operator.apply(yaw);
        pitch = operator.apply(pitch);
        return this;
    }

    @Override
    @Destructive
    public DualAxisRotationBuilder calculate(DualAxisRotationBuilder other, BiFunction<Float, Float, Float> operator) {
        yaw = operator.apply(yaw, other.yaw);
        pitch = operator.apply(pitch, other.pitch);
        return this;
    }

    @Override
    @Destructive
    public DualAxisRotationBuilder calculate(DualAxisRotationBuilder other1, DualAxisRotationBuilder other2, TriFunction<Float, Float, Float, Float> operator) {
        this.yaw = operator.apply(yaw, other1.yaw, other2.yaw);
        this.pitch = operator.apply(pitch, other1.pitch, other2.pitch);
        return this;
    }

    @Override
    @Destructive
    public DualAxisRotationBuilder add(DualAxisRotationBuilder addend) {
        return calculate(addend, Float::sum);
    }

    @Override
    @Destructive
    public DualAxisRotationBuilder subtract(DualAxisRotationBuilder subtrahend) {
        return calculate(subtrahend, (a, b) -> a - b);
    }

    /**
     * この回転を実数倍します。
     * @param scalar 倍率
     * @return this
     */
    @Override
    @Destructive
    public DualAxisRotationBuilder scale(Float scalar) {
        return calculate(component -> component * scalar);
    }

    /**
     * この回転を逆向きにします。
     * @return this
     */
    @Override
    @Destructive
    public DualAxisRotationBuilder invert() {
        yaw += 180;
        pitch *= -1;
        return this;
    }

    @Override
    @Destructive
    public DualAxisRotationBuilder clamp(DualAxisRotationBuilder min, DualAxisRotationBuilder max) {
        return calculate(min, max, (value, minValue, maxValue) -> Math.max(minValue, Math.min(value, maxValue)));
    }

    public String format(String format, int digits) {
        final String floatFormat = "%." + digits + "f";

        final String yaw = String.format(floatFormat, this.yaw);
        final String pitch = String.format(floatFormat, this.pitch);

        return format
            .replaceAll("\\$x", yaw)
            .replaceAll("\\$y", pitch)
            .replaceFirst("\\$c", yaw)
            .replaceFirst("\\$c", pitch)
            .replaceAll("\\$c", "");
    }

    @Override
    public String toString() {
        return format("($x, $y)", 2);
    }

    @Override
    public DualAxisRotationBuilder copy() {
        return new DualAxisRotationBuilder(yaw, pitch);
    }

    @Override
    public boolean isZero() {
        return equals(new DualAxisRotationBuilder());
    }

    /**
     * この回転と別の回転がなす角の大きさを求めます。
     * @param other 別の回転
     * @return 角の大きさ(度)
     */
    public double getAngleBetween(DualAxisRotationBuilder other) {
        return getDirection3d().getAngleBetween(other.getDirection3d());
    }

    /**
     * この回転を単位ベクトルに変換します。
     * @return 単位ベクトル
     */
    public Vector3Builder getDirection3d() {
        final double x = -Math.sin(yaw * Math.PI / 180) * Math.cos(pitch * Math.PI / 180);
        final double y = -Math.sin(pitch * Math.PI / 180);
        final double z = Math.cos(yaw * Math.PI / 180) * Math.cos(pitch * Math.PI / 180);

        return new Vector3Builder(x, y, z);
    }

    public Location toLocation(Location location) {
        return new Location(location.getWorld(), location.x(), location.y(), location.z(), yaw, pitch);
    }

    public Location toLocation(Vector3Builder coordinate, World world) {
        return new Location(world, coordinate.x(), coordinate.y(), coordinate.z(), yaw, pitch);
    }

    public static DualAxisRotationBuilder from(Location location) {
        return new DualAxisRotationBuilder(location.getYaw(), location.getPitch());
    }

    public static DualAxisRotationBuilder from(Entity entity) {
        return DualAxisRotationBuilder.from(entity.getLocation());
    }

    public static DualAxisRotationBuilder north() {
        return new DualAxisRotationBuilder(180, 0);
    }

    public static DualAxisRotationBuilder south() {
        return new DualAxisRotationBuilder(0, 0);
    }

    public static DualAxisRotationBuilder east() {
        return new DualAxisRotationBuilder(-90, 0);
    }

    public static DualAxisRotationBuilder west() {
        return new DualAxisRotationBuilder(90, 0);
    }

    public static DualAxisRotationBuilder up() {
        return new DualAxisRotationBuilder(0, -90);
    }

    public static DualAxisRotationBuilder down() {
        return new DualAxisRotationBuilder(0, 90);
    }
}
