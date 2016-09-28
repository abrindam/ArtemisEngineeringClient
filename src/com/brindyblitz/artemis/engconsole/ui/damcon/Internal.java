package com.brindyblitz.artemis.engconsole.ui.damcon;

import com.walkertribe.ian.vesseldata.VesselNode;

import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;

public abstract class Internal {
    protected static final float SCALE = 0.0034f;

    protected Shape3D shape;

    public Internal() { }

    protected static Point3f internalPositionToWorldSpace(float x, float y, float z) {
        Point3f p = new Point3f(-x, y, z);
        p.scale(SCALE);
        return p;
    }

    protected static Point3f internalPositionToWorldSpace(VesselNode vn) {
        return internalPositionToWorldSpace(vn.getX(), vn.getY(), vn.getZ());
    }

    public Shape3D getShape() {
        return shape;
    }
}