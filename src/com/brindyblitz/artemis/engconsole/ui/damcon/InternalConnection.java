package com.brindyblitz.artemis.engconsole.ui.damcon;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

import com.brindyblitz.artemis.utils.AudioManager;

import net.dhleong.acl.vesseldata.VesselNodeConnection;

public class InternalConnection extends Internal {
	private VesselNodeConnection vesselNodeConnection;
	private LineArray lineArray;

	private static final Color3f LINE_COLOR = new Color3f(0f, 1f, 0f);
	private static final float ALPHA_STANDARD = 0.7f;

	public InternalConnection(VesselNodeConnection vessel_node_connection, AudioManager audio_manager) {
		super(audio_manager);

		this.vesselNodeConnection = vessel_node_connection;

		lineArray = new LineArray(2, LineArray.COORDINATES);
		lineArray.setCoordinate(0, Internal.internalPositionToWorldSpace(this.vesselNodeConnection.getNode1()));
		lineArray.setCoordinate(1, Internal.internalPositionToWorldSpace(this.vesselNodeConnection.getNode2()));
		lineArray.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

		this.shape = new Shape3D(lineArray, generateAppearance(LineAttributes.PATTERN_SOLID));
		this.shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
	}

	public Appearance generateAppearance(int pattern) {
		Appearance appearance = new Appearance();	// TODO: PERF > don't get new appearance every time

		// Set transparency
		TransparencyAttributes transparency =  new TransparencyAttributes(TransparencyAttributes.NICEST, ALPHA_STANDARD);
		appearance.setTransparencyAttributes(transparency);

		// Enable automatic anti-aliasing
		LineAttributes line_attributes = new LineAttributes();
		line_attributes.setLineAntialiasingEnable(true);
		line_attributes.setLinePattern(pattern);
		appearance.setLineAttributes(line_attributes);

		// Set color
		ColoringAttributes coloring_attributes = new ColoringAttributes();
		coloring_attributes.setColor(LINE_COLOR);
		appearance.setColoringAttributes(coloring_attributes);

		return appearance;
	}
}