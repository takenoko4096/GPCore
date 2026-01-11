package com.gmail.subnokoii78.gpcore.shape;

import com.gmail.subnokoii78.gpcore.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.gpcore.vector.Vector3Builder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@NullMarked
public abstract class ShapeBase {
    protected World world;

    protected final Vector3Builder center = new Vector3Builder();

    protected final TripleAxisRotationBuilder rotation = new TripleAxisRotationBuilder();

    protected ShapeBase() {
        world = Bukkit.getWorlds().getFirst();
    }

    @Nullable
    private ParticleSpawner<?> particle;

    private float scale = 1.0f;

    private float density = 1.0f;

    private final Set<Consumer<Vector3Builder>> consumers = new HashSet<>();

    public final void put(World world, Vector3Builder center) {
        this.world = world;
        this.center.x(center.x());
        this.center.y(center.y());
        this.center.z(center.z());
    }

    public final void put(World world) {
        this.world = world;
    }

    public final void put(Vector3Builder center) {
        this.center.x(center.x());
        this.center.y(center.y());
        this.center.z(center.z());
    }

    public final void rotate(TripleAxisRotationBuilder rotation) {
        this.rotation.add(rotation);
    }

    public final @Nullable ParticleSpawner<?> getParticle() {
        return particle;
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
        final Vector3Builder position = center.copy().add(relativePos.copy().scale((double) scale));
        if (particle != null) particle.place(world, position).spawn();
        consumers.forEach(consumer -> consumer.accept(position));
    }

    public void onDot(Consumer<Vector3Builder> consumer) {
        consumers.add(consumer);
    }

    public abstract void draw();
}
