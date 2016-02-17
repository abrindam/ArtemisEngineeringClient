package com.brindyblitz.artemis.engconsole.ui.damcon;

import com.brindyblitz.artemis.utils.AudioManager;
import net.dhleong.acl.vesseldata.VesselNodeConnection;

import javax.media.j3d.*;
import javax.vecmath.Color3f;

public class InternalConnection extends Internal {
    private VesselNodeConnection vesselNodeConnection;
    private LineArray lineArray;

    private static final Color3f LINE_COLOR = new Color3f(0f, 1f, 0f);
    private static final float ALPHA_STANDARD = 0.1F, ALPHA_HOVERED = 0.9f;
    // TODO: > need to do this for hallway selection as well, for picking even if they're not visible?

    public InternalConnection(VesselNodeConnection vessel_node_connection, AudioManager audio_manager) {
        super(audio_manager);

        this.vesselNodeConnection = vessel_node_connection;

        lineArray = new LineArray(2, LineArray.COORDINATES);
        lineArray.setCoordinate(0, Internal.internalPositionToWorldSpace(this.vesselNodeConnection.getNode1()));
        lineArray.setCoordinate(1, Internal.internalPositionToWorldSpace(this.vesselNodeConnection.getNode2()));
        lineArray.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

        Appearance appearance = new Appearance();

        // Set transparency
        TransparencyAttributes transparency =  new TransparencyAttributes(TransparencyAttributes.NICEST, ALPHA_STANDARD);
        appearance.setTransparencyAttributes(transparency);

        // Enable automatic anti-aliasing
        LineAttributes line_attributes = new LineAttributes();
        line_attributes.setLineAntialiasingEnable(true);
        line_attributes.setLinePattern(LineAttributes.PATTERN_SOLID);
        appearance.setLineAttributes(line_attributes);

        // Set color
        ColoringAttributes coloring_attributes = new ColoringAttributes();
        coloring_attributes.setColor(LINE_COLOR);
        appearance.setColoringAttributes(coloring_attributes);

        this.shape = new Shape3D(lineArray, appearance);
        this.shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    }
}