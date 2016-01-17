package com.brindyblitz.artemis.engconsole.ui.damcon;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import net.dhleong.acl.vesseldata.VesselNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;

public class InternalNode {
    private static final float RADIUS = 0.05f, SCALE = 0.0034f;

    private BranchGroup branchGroup;
    private VesselNode vesselNode;
    private Sphere sphere;

    public InternalNode(VesselNode vessel_node) {
        this.vesselNode = vessel_node;

        this.branchGroup = new BranchGroup();

        if (this.vesselNode.getSystem() != null) {
            Vector3f pos = new Vector3f(-vessel_node.getX(), vessel_node.getY(), vessel_node.getZ());
            pos.scale(SCALE);

            sphere = new Sphere(RADIUS, appearanceFromHealthPercentage(1f));
            // sphere = new Sphere(RADIUS, Shape3D.ALLOW_APPEARANCE_WRITE | Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE, appearanceFromHealthPercentage(1f));
            /*sphere.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            sphere.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
            sphere.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            sphere.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);*/
            sphere.getShape(Sphere.BODY).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

            sphere.setCapability(Shape3D.ENABLE_PICK_REPORTING);
            sphere.setPickable(true);

            Transform3D transform = new Transform3D();
            transform.setTranslation(pos);

            TransformGroup tg = new TransformGroup();
            tg.setTransform(transform);
            tg.addChild(sphere);

            this.branchGroup.addChild(tg);
        }
    }

    public void updateHealth(float pct) {
        sphere.setAppearance(appearanceFromHealthPercentage(pct));
    }

    private static Appearance appearanceFromHealthPercentage(float pct) {
        Color color = Color.getHSBColor(getEmptyHue() - (getEmptyHue() - getFullHue()) * pct, 1, 1);

        // TODO: cleanup!

        Color3f ambientColour = new Color3f(0f, 0f, 0f);
        Color3f emissiveColour = new Color3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        Color3f specularColour = new Color3f(0f, 0f, 0f);
        Color3f diffuseColour = new Color3f(0f, 0f, 0f);
        float shininess = 0.0f;
        Appearance app = new Appearance();
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        app.setMaterial(new Material(ambientColour, emissiveColour, diffuseColour, specularColour, shininess));
        TransparencyAttributes transparency =  new TransparencyAttributes(TransparencyAttributes.NICEST, .5f);  // TODO: when hovering, make opaque
        app.setTransparencyAttributes(transparency);
        return app;
    }

    private static float getFullHue() {
        return 120f / 360f;
    }

    private static float getEmptyHue() {
        return 0f;
    }
    // TODO: this color stuff is duplicated in SystemHealthSlider.java, fix it!

    private InternalNode(Vector3d position, int IDONTWORKYET) {
        if (true) {
            throw new NotImplementedException();
        }

        /////

        ColorCube color_cube = new ColorCube(0.05d);
        branchGroup = new BranchGroup();
        branchGroup.addChild(color_cube);

        //////

        Transform3D xform = new Transform3D();
        xform.set(position);

        TransformGroup xformGroup = new TransformGroup();
        xformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        xformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        xformGroup.setTransform(xform);

        ColorCube rightCube = new ColorCube();
        xformGroup.addChild(rightCube);

        /*Appearance app = new Appearance();
        Color3f ambientColour = new Color3f(1.0f, 1.0f, 0.0f);
        Color3f emissiveColour = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f specularColour = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f diffuseColour = new Color3f(1.0f, 1.0f, 0.0f);
        float shininess = 20.0f;
        app.setMaterial(new Material(ambientColour, emissiveColour,
                diffuseColour, specularColour, shininess));
        Box leftCube = new Box(1.0f, 1.0f, 1.0f, app);
        xformGroup.addChild(leftCube);*/

        Billboard billboard = new Billboard(xformGroup, Billboard.ROTATE_ABOUT_AXIS, new Vector3f(0f, 1f, 0f));

        branchGroup = new BranchGroup();
        branchGroup.addChild(billboard);
    }

    public BranchGroup getBranchGroup() {
        return branchGroup;
    }
}