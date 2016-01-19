package com.brindyblitz.artemis.engconsole.ui.damcon;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.sun.j3d.utils.geometry.Sphere;
import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager.EnhancedDamconStatus;

public class InternalTeam extends InternalSelectable {
	private TransformGroup transformGroup;
    private EnhancedDamconStatus status;

    // TODO: use a Box instead of a sphere or something (set every side to be pickable or use billboards)

    public InternalTeam(EngineeringConsoleManager.EnhancedDamconStatus damcon_status) {
        this.status = damcon_status;

        alpha = 0.1f;

        this.branchGroup = new BranchGroup();

        this.sphere = new Sphere(RADIUS, appearanceFromHealthPercentage(1f, false));

        Shape3D shape = this.sphere.getShape(Sphere.BODY);
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        setPickable(shape);

        this.transformGroup = new TransformGroup();
        updatePos(this.status.getX(), this.status.getY(), this.status.getZ());
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

    @Override
    public Appearance appearanceFromHealthPercentage(float pct, boolean invisible) {
        Appearance app = super.appearanceFromHealthPercentage(pct, invisible);
        if (!invisible) {
            // TODO: > damcon team coloration?
            app.setMaterial(new Material(BLACK, new Color3f(0f, 0f, 1f), BLACK, BLACK, SHININESS));
        }
        return app;
    }

    @Override
    public String toString() {
        return this.status.toString();
    }
}