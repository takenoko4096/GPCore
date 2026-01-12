package com.gmail.subnokoii78.gpcore.shape;

import com.gmail.subnokoii78.gpcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.gpcore.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.gpcore.vector.Vector3Builder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class VectorPrinter {
    private final World dimension;

    private final Vector3Builder position;

    public VectorPrinter(World dimension, Vector3Builder position) {
        this.dimension = dimension;
        this.position = position;
    }

    public VectorPrinter(Location location) {
        this.dimension = location.getWorld();
        this.position = Vector3Builder.from(location);
    }

    public void print(Vector3Builder vector3, Color color) {
        dimension.spawnParticle(
            Particle.FLAME,
            position.withWorld(dimension),
            1,
            0.0d, 0.0d, 0.0d,
            0.0d
        );

        dimension.spawnParticle(
            Particle.SOUL_FIRE_FLAME,
            position.copy().add(vector3).withWorld(dimension),
            1,
            0.0d, 0.0d, 0.0d,
            0.0d
        );

        new ShapeTemplate()
            .scale((float) vector3.length())
            .world(dimension)
            .center(position)
            .particle(ParticleSpawner.dust(color, 0.5f))
            .rotation(TripleAxisRotationBuilder.from(vector3.getRotation2f()))
            .newShape(StraightLine.class)
            .draw();
    }

    public void print(DualAxisRotationBuilder rotation2, Color color) {
        print(rotation2.getDirection3d(), color);
    }

    public void print(TripleAxisRotationBuilder rotation3, Color color) {
        print(rotation3.getDirection3d(), color);
    }
}
