package com.brindyblitz.artemis.engconsole.ui.damcon;

import net.dhleong.acl.vesseldata.VesselNodeConnection;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import java.awt.*;

public class InternalHallway extends Internal {
    private VesselNodeConnection vesselNodeConnection;
    private LineArray lineArray;
    private Shape3D shape;

    public InternalHallway(VesselNodeConnection vessel_node_connection) {
        alpha = 0.1f;

        this.vesselNodeConnection = vessel_node_connection;

        lineArray = new LineArray(2, LineArray.COORDINATES);
        lineArray.setCoordinate(0, Internal.vesselNodePosition(this.vesselNodeConnection.getNode1()));
        lineArray.setCoordinate(1, Internal.vesselNodePosition(this.vesselNodeConnection.getNode2()));
        lineArray.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

        this.shape = new Shape3D(lineArray, appearanceFromHealthPercentage(1f));
        this.shape.setPickable(true);
    }

    @Override
    public void updateHealth(float pct) {
        shape.setAppearance(appearanceFromHealthPercentage(pct));
    }

    @Override
    protected Appearance appearanceFromHealthPercentage(float pct) {
        Appearance appearance = new Appearance();

        // Set transparency
        TransparencyAttributes transparency =  new TransparencyAttributes(TransparencyAttributes.NICEST, alpha);
        appearance.setTransparencyAttributes(transparency);

        // Enable automatic anti-aliasing
        LineAttributes line_attributes = new LineAttributes();
        line_attributes.setLineAntialiasingEnable(true);
        line_attributes.setLinePattern(LineAttributes.PATTERN_SOLID);
        appearance.setLineAttributes(line_attributes);

        // Set color
        ColoringAttributes coloring_attributes = new ColoringAttributes();
        coloring_attributes.setColor(getColorFromHealth(pct));
        appearance.setColoringAttributes(coloring_attributes);

        return appearance;
    }

    public Shape3D getShape() {
        return this.shape;
    }
}