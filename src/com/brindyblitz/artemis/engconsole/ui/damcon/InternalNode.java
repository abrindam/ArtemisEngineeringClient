package com.brindyblitz.artemis.engconsole.ui.damcon;

import java.awt.Color;

import com.brindyblitz.artemis.utils.AudioManager;

import com.walkertribe.ian.util.GridCoord;
import com.walkertribe.ian.vesseldata.VesselNode;

public class InternalNode extends InternalSelectable {
	private VesselNode vesselNode;
	private boolean isSystemNode;

	private static final String HUD_ICON = "hud/inner_circle.png";
	private static final Color SYSTEM_NODE_COLOR = new Color(0, 255, 0, 100),
			NON_SYSTEM_NODE_COLOR = new Color(255, 255, 255);
	private static final int SYSTEM_NODE_STANDARD_ALPHA = 175,
			SYSTEM_NODE_HOVERED_ALPHA = 250,
			NON_SYSTEM_NODE_STANDARD_ALPHA = 0,
			NON_SYSTEM_NODE_HOVERED_ALPHA = 200;
	private static final float ICON_DIM_SYSTEM_NODE = 0.05f,
			ICON_DIM_NON_SYSTEM_NODE = 0.03f;

	public InternalNode(VesselNode vessel_node, boolean is_system_node, Damcon damcon) {
		super(is_system_node ? "System Node" : "Non-System Node",
				HUD_ICON,
				is_system_node ? SYSTEM_NODE_COLOR : NON_SYSTEM_NODE_COLOR,
				is_system_node ? SYSTEM_NODE_STANDARD_ALPHA : NON_SYSTEM_NODE_STANDARD_ALPHA,
				is_system_node ? SYSTEM_NODE_HOVERED_ALPHA : NON_SYSTEM_NODE_HOVERED_ALPHA,
				is_system_node ? ICON_DIM_SYSTEM_NODE : ICON_DIM_NON_SYSTEM_NODE,
				damcon);

		this.vesselNode = vessel_node;
		this.isSystemNode = is_system_node;

		updatePos(vessel_node.getX(), vessel_node.getY(), vessel_node.getZ());
	}

	@Override
	protected boolean visible() {
		return this.isSystemNode;
		// TODO: Make damaged non-system nodes visible at all times, and maybe add a checkbox to hide them, or show only damaged nodes
	}

	@Override
	protected boolean selected() {
		return false;
	}

	public static boolean isSystemNode(VesselNode vn) {
		return vn.getSystem() != null;
	}

	public GridCoord getGridCoords() {
		return this.vesselNode.getGridCoord();
	}

	@Override
	public String toString() {
		return "Node: " + this.vesselNode;
	}
}