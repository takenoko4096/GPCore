package com.gmail.subnokoii78.gpcore.shape;

import com.gmail.subnokoii78.gpcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.gpcore.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.gpcore.vector.Vector3Builder;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.InvocationTargetException;

@NullMarked
public final class ShapeTemplate {
    private final ShapeBase base = new ShapeBase() {
        @Override
        public void draw() {}
    };

    public ShapeTemplate() {}

    public ShapeTemplate put(Location location) {
        world(location.getWorld());
        center(Vector3Builder.from(location));
        rotation(TripleAxisRotationBuilder.from(DualAxisRotationBuilder.from(location)));
        return this;
    }

    public ShapeTemplate world(World world) {
        base.setDimension(world);
        return this;
    }

    public ShapeTemplate center(Vector3Builder position) {
        base.setPosition(position);
        return this;
    }

    public ShapeTemplate rotation(TripleAxisRotationBuilder rotation) {
        base.setRotation(rotation);
        return this;
    }

    public ShapeTemplate particle(ParticleSpawner<?> particle) {
        base.setParticle(particle);
        return this;
    }

    public ShapeTemplate scale(float scale) {
        base.setScale(scale);
        return this;
    }

    public ShapeTemplate density(float density) {
        base.setDensity(density);
        return this;
    }

    public <T extends ShapeBase> T newShape(Class<T> clazz) {
        final T instance;
        try {
            instance = clazz.getConstructor().newInstance();
        }
        catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalArgumentException("コンストラクターを呼び出せないクラスが渡されました");
        }

        instance.setDimension(base.getDimension());
        instance.setPosition(base.getPosition());
        instance.setRotation(base.getRotation());
        instance.setParticle(base.getParticle());
        instance.setScale(base.getScale());
        instance.setDensity(base.getDensity());

        return instance;
    }
}
