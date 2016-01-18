package com.brindyblitz.artemis.engconsole.ui.damcon;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Sphere;

public class InternalTeam extends InternalSelectable {
	private TransformGroup transformGroup;

    // TODO: use a Box instead of a sphere or something (set every side to be pickable or use billboards)

    public InternalTeam(float x, float y, float z) {
        alpha = 0.5f;

        this.branchGroup = new BranchGroup();

        this.sphere = new Sphere(RADIUS, appearanceFromHealthPercentage(1f, false));

        Shape3D shape = this.sphere.getShape(Sphere.BODY);
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        setPickable(shape);

        this.transformGroup = new TransformGroup();
        updatePos(x, y, z);
        this.transformGroup.addChild(sphere);
        this.transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        this.branchGroup.addChild(transformGroup);
    }

    public void updatePos(float x, float y, float z) {
        Point3f pos = Internal.internalPositionToWorldSpace(x, y, z);
        Transform3D transform = new Transform3D();
        transform.setTranslation(new Vector3f(pos));
        this.transformGroup.setTransform(transform);
    }

    // TODO: DAMCON API > DAMCON API needs to get health based on remaining / default team member counts

    @Override
    public void updateHealth(float pct) {
        super.updateHealth(pct);
        sphere.setAppearance(appearanceFromHealthPercentage(pct, false));
    }
}