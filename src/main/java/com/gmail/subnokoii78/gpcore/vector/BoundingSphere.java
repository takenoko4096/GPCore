package com.gmail.subnokoii78.gpcore.vector;

import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BoundingSphere {
    private final Vector3Builder center;

    private final double radius;

    public BoundingSphere(Vector3Builder center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public boolean isInside(Vector3Builder point) {
        return center.getDistanceTo(point) <= radius;
    }

    public boolean isCollides(BoundingSphere other) {
        return center.getDistanceTo(other.center) <= radius + other.radius;
    }

    public double getShortestDistance(OrientedBoundingBox box) {
        final TripleAxisRotationBuilder.ObjectCoordsSystem system = box.rotation().getObjectsCoordsSystem();
        final Vector3Builder size = box.size();

        final Vector3Builder v = new Vector3Builder();

        int i = 0;
        for (final double l : size.components()) {
            final Vector3Builder a = center.copy().subtract(box.center());
            final Vector3Builder b;

            if (i == 0) b = system.getX();
            else if (i == 1) b = system.getY();
            else if (i == 2) b = system.getZ();
            else throw new IllegalStateException("NEVER HAPPENS");

            final double s = Math.abs(a.dot(b) / l);
            if (s > 1) {
                v.add(b.scale(l * (1 - s)));
            }

            i++;
        }

        return v.length();
    }

    public boolean isCollides(OrientedBoundingBox box) {
        return getShortestDistance(box) <= radius;
    }
}
