package com.gmail.subnokoii78.gpcore.shape;

import com.gmail.subnokoii78.gpcore.vector.Vector3Builder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class ParticleSpawner<T> {
    private World world;

    private final Vector3Builder center = new Vector3Builder();

    private final Particle particle;

    private final Vector3Builder delta = new Vector3Builder();

    private int count = 1;

    private double speed = 0;

    private final List<Player> receivers = new ArrayList<>();

    @Nullable
    private final T data;

    public ParticleSpawner(Particle particle, @Nullable T data) {
        this.particle = particle;
        this.data = data;
        this.world = Bukkit.getWorlds().getFirst();
    }

    public ParticleSpawner(Particle particle) {
        this(particle, null);
    }

    public ParticleSpawner<T> place(World world, Vector3Builder center) {
        this.world = world;
        this.center.x(center.x());
        this.center.y(center.y());
        this.center.z(center.z());
        return this;
    }

    public ParticleSpawner<T> place(Location location) {
        return place(location.getWorld(), Vector3Builder.from(location));
    }

    public ParticleSpawner<T> delta(Vector3Builder delta) {
        this.delta.x(delta.x());
        this.delta.y(delta.y());
        this.delta.z(delta.z());
        return this;
    }

    public ParticleSpawner<T> count(int count) {
        this.count = count;
        return this;
    }

    public ParticleSpawner<T> speed(double speed) {
        this.speed = speed;
        return this;
    }

    public ParticleSpawner<?> receivers(List<Player> players) {
        receivers.addAll(players);
        return this;
    }

    public void spawn() {
        if (receivers.isEmpty()) {
            world.spawnParticle(particle, center.withWorld(world), count, delta.x(), delta.y(), delta.z(), speed, data);
        }
        else {
            for (final Player receiver : receivers) {
                receiver.spawnParticle(particle, center.withWorld(world), count, delta.x(), delta.y(), delta.z(), speed, data);
            }
        }
    }

    public static DustSpawner dust(Color color, float size) {
        return new DustSpawner(new Particle.DustOptions(color, size));
    }

    public static DustTransitionSpawner dustTransition(Color from, Color to, float size) {
        return new DustTransitionSpawner(new Particle.DustTransition(from, to, size));
    }

    public static ParticleSpawner<?> of(Particle particle) {
        return new ParticleSpawner<>(particle);
    }
}
