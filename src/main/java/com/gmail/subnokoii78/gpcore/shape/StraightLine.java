package com.gmail.subnokoii78.gpcore.shape;

import com.gmail.subnokoii78.gpcore.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.gpcore.vector.Vector3Builder;
import org.bukkit.util.RayTraceResult;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class StraightLine extends ShapeBase {
    @Override
    public void draw() {
        final double max = 20 * getDensity();
        for (int i = 0; i < max; i++) {
            dot(getRotation().getDirection3d().length(i / max));
        }
    }

    public final void set(Vector3Builder from, Vector3Builder to) {
        setPosition(from);
        setRotation(TripleAxisRotationBuilder.from(from.getDirectionTo(to).getRotation2f()));
        setScale((float) from.getDistanceTo(to));
    }

    public final @Nullable RayTraceResult rayTrace() {
        return getDimension().rayTraceEntities(
            getPosition().withWorld(getDimension()),
            getRotation().getDirection3d().toBukkitVector(),
            getScale()
        );
    }
}
