package com.brindyblitz.artemis.engconsole.ui.damcon;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Sphere;

public class InternalTeam {
    private static final float RADIUS = 0.05f, SCALE = 0.0034f;

    private BranchGroup branchGroup;
    private Sphere sphere;

	private TransformGroup tg;

    public InternalTeam(float x, float y, float z) {

        this.branchGroup = new BranchGroup();

        sphere = new Sphere(RADIUS, appearance());

        Shape3D shape = sphere.getShape(Sphere.BODY);
        shape.setCapability(Shape3D.ENABLE_PICK_REPORTING);
        shape.setPickable(true);

        tg = new TransformGroup();
        updatePos(x, y, z);
        tg.addChild(sphere);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        this.branchGroup.addChild(tg);
    }

    public void updatePos(float x, float y, float z) {
    	Vector3f pos = new Vector3f(-x, y, z);
        pos.scale(SCALE);
        Transform3D transform = new Transform3D();
        transform.setTranslation(pos);
        
        tg.setTransform(transform);
    }

    private static Appearance appearance() {
        // TODO: cleanup!

        Color3f ambientColour = new Color3f(0f, 0f, 0f);
        Color3f emissiveColour = new Color3f(1f, 1f, 1f);
        Color3f specularColour = new Color3f(0f, 0f, 0f);
        Color3f diffuseColour = new Color3f(0f, 0f, 0f);
        float shininess = 0.0f;
        Appearance app = new Appearance();
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        app.setMaterial(new Material(ambientColour, emissiveColour, diffuseColour, specularColour, shininess));
        TransparencyAttributes transparency =  new TransparencyAttributes(TransparencyAttributes.NICEST, .5f);  // TODO: FILE ISSUE > when hovering, make opaque
        app.setTransparencyAttributes(transparency);
        return app;
    }

    public BranchGroup getBranchGroup() {
        return branchGroup;
    }

    // TODO: > clean this up to bring in line with other Internal classes
}