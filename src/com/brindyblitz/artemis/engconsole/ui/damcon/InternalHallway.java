package com.brindyblitz.artemis.engconsole.ui.damcon;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import net.dhleong.acl.vesseldata.VesselNode;
import net.dhleong.acl.vesseldata.VesselNodeConnection;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.awt.*;

public class InternalHallway {
    private static final float RADIUS = 0.05f, SCALE = 0.0034f; // TODO: unify scale in Damcon.java or make these subclasses with access to that

    private VesselNodeConnection vesselNodeConnection;
    private LineArray lineArray;
    private Shape3D shape;

    public InternalHallway(VesselNodeConnection vessel_node_connection) {
        this.vesselNodeConnection = vessel_node_connection;

        VesselNode vn1 = this.vesselNodeConnection.getNode1(),
                   vn2 = this.vesselNodeConnection.getNode2();

        Point3f p1 = new Point3f(-vn1.getX(), vn1.getY(), vn1.getZ());
        Point3f p2 = new Point3f(-vn2.getX(), vn2.getY(), vn2.getZ());
        p1.scale(SCALE);
        p2.scale(SCALE);

        lineArray = new LineArray(2, LineArray.COORDINATES);
        lineArray.setCoordinate(0, p1);
        lineArray.setCoordinate(1, p2);
        lineArray.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

        this.shape = new Shape3D(lineArray, appearanceFromHealthPercentage(1f));
    }

    public void updateHealth(float pct) {
        // lineArray.setAppearance(appearanceFromHealthPercentage(pct));
        // TODO: implement
    }

    private static Appearance appearanceFromHealthPercentage(float pct) {
        Appearance appearance = new Appearance();

        // Set transparency
        TransparencyAttributes transparency =  new TransparencyAttributes(TransparencyAttributes.NICEST, .1f);
        appearance.setTransparencyAttributes(transparency);

        // Enable automatic anti-aliasing
        LineAttributes line_attributes = new LineAttributes();
        line_attributes.setLineAntialiasingEnable(true);
        line_attributes.setLinePattern(LineAttributes.PATTERN_SOLID);
        appearance.setLineAttributes(line_attributes);

        // Set color
        Color awtColor = Color.getHSBColor(getEmptyHue() - (getEmptyHue() - getFullHue()) * pct, 1, 1);
        Color3f color = new Color3f(awtColor);
        ColoringAttributes coloring_attributes = new ColoringAttributes();
        coloring_attributes.setColor(color);
        appearance.setColoringAttributes(coloring_attributes);

        LineArray la = new LineArray(2, LineArray.COORDINATES);
        la.setCoordinate(0, new Point3f(0f, 0f, 0f));
        la.setCoordinate(1, new Point3f(1f, 0f, 0f));
        return appearance;
    }

    private static float getFullHue() {
        return 120f / 360f;
    }

    private static float getEmptyHue() {
        return 0f;
    }
    // TODO: this color stuff is duplicated in SystemHealthSlider.java, fix it!

    public Shape3D getShape() {
        return this.shape;
    }
}