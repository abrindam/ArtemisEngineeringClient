package com.brindyblitz.artemis.engconsole.ui.damcon;

import net.dhleong.acl.util.GridCoord;
import net.dhleong.acl.vesseldata.VesselNode;

import java.awt.*;

public class InternalNode extends InternalSelectable {
    private VesselNode vesselNode;
    private boolean isSystemNode;

    private static final Color SYSTEM_NODE_COLOR = new Color(0, 255, 0, 100),
                               NON_SYSTEM_NODE_COLOR = new Color(255, 255, 255);
    private static final int SYSTEM_NODE_STANDARD_ALPHA = 100,
                             SYSTEM_NODE_HOVERED_ALPHA = 225,
                             NON_SYSTEM_NODE_STANDARD_ALPHA = 0,
                             NON_SYSTEM_NODE_HOVERED_ALPHA = 200;
    private static final float ICON_DIM_SYSTEM_NODE = 0.05f,
                               ICON_DIM_NON_SYSTEM_NODE = 0.03f;

    public InternalNode(VesselNode vessel_node, boolean is_system_node) {
        super("hud/inner_circle.png",
              is_system_node ? SYSTEM_NODE_COLOR : NON_SYSTEM_NODE_COLOR,
              is_system_node ? SYSTEM_NODE_STANDARD_ALPHA : NON_SYSTEM_NODE_STANDARD_ALPHA,
              is_system_node ? SYSTEM_NODE_HOVERED_ALPHA : NON_SYSTEM_NODE_HOVERED_ALPHA,
              is_system_node ? ICON_DIM_SYSTEM_NODE : ICON_DIM_NON_SYSTEM_NODE);

        this.vesselNode = vessel_node;
        this.isSystemNode = is_system_node;

        updatePos(-vessel_node.getX(), vessel_node.getY(), vessel_node.getZ());
    }

    @Override
    protected boolean visible() {
        return this.isSystemNode;
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