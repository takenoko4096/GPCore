package com.gmail.subnokoii78.gpcore.shape;

import com.gmail.subnokoii78.gpcore.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.gpcore.vector.Vector3Builder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class Rhombus extends ShapeBase {
    @Override
    public void draw() {
        final TripleAxisRotationBuilder.ObjectCoordsSystem axes = getRotation().getObjectsCoordsSystem();

        var left = axes.getX();
        var right = axes.getX().invert();
        var up = axes.getY();
        var down = axes.getY().invert();

        lineFromTo(down, left);
        lineFromTo(left, up);
        lineFromTo(up,right);
        lineFromTo(right, down);
    }

    protected void lineFromTo(Vector3Builder from, Vector3Builder to) {
        final StraightLine line = new StraightLine();
        line.setDimension(getDimension());
        line.set(from, to);
        line.draw();
    }
}
