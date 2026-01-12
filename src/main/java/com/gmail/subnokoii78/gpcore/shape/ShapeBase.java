package com.gmail.subnokoii78.gpcore.shape;

import com.gmail.subnokoii78.gpcore.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.gpcore.vector.Vector3Builder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public abstract class ShapeBase {
    private World world;

    private final Vector3Builder position = new Vector3Builder();

    private final TripleAxisRotationBuilder rotation = new TripleAxisRotationBuilder();

    protected ShapeBase() {
        world = Bukkit.getWorlds().getFirst();
    }

    @Nullable
    private ParticleSpawner<?> particle;

    private float scale = 1.0f;

    private float density = 1.0f;

    public final void put(Location location) {
        this.world = location.getWorld();
        this.position.x(location.x());
        this.position.y(location.y());
        this.position.z(location.z());
        this.rotation.yaw(location.getYaw());
        this.rotation.pitch(location.getPitch());
    }

    public final World getDimension() {
        return world;
    }

    public final void setDimension(World world) {
        this.world = world;
    }

    public final Vector3Builder getPosition() {
        return position.copy();
    }

    public final void setPosition(Vector3Builder center) {
        this.position.x(center.x());
        this.position.y(center.y());
        this.position.z(center.z());
    }

    public final TripleAxisRotationBuilder getRotation() {
        return rotation.copy();
    }

    public final void setRotation(TripleAxisRotationBuilder rotation) {
        this.rotation.yaw(rotation.yaw());
        this.rotation.pitch(rotation.pitch());
        this.rotation.roll(rotation.roll());
    }

    public final Location getLocation() {
        return new Location(world, position.x(), position.y(), position.z(), rotation.yaw(), rotation.pitch());
    }

    public final @Nullable ParticleSpawner<?> getParticle() {
        return particle == null ? null : particle.copy();
    }

    public final void setParticle(@Nullable ParticleSpawner<?> particle) {
        this.particle = particle;
    }

    public final float getScale() {
        return scale;
    }

    public final void setScale(float scale) {
        this.scale = scale;
    }

    public final float getDensity() {
        return density;
    }

    public final void setDensity(float density) {
        this.density = density;
    }

    protected final void dot(Vector3Builder relativePos) {
        final Vector3Builder position = this.position.copy()
            .add(
                relativePos.copy().scale((double) scale)
            );

        if (particle != null) {
            particle.place(world, position).spawn();
        }
    }

    public abstract void draw();
}
