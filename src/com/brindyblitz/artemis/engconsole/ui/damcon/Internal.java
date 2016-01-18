package com.brindyblitz.artemis.engconsole.ui.damcon;


import com.brindyblitz.artemis.engconsole.ui.SystemStatusRenderer;
import com.brindyblitz.artemis.engconsole.ui.SystemStatusSlider;
import net.dhleong.acl.vesseldata.VesselNode;

import javax.media.j3d.Appearance;
import javax.media.j3d.Node;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import java.awt.*;

public abstract class Internal {
    protected static final float SCALE = 0.0034f;
    protected static final float SHININESS = 0f;
    protected float alpha;


    protected static Point3f vesselNodePosition(VesselNode vn) {
        Point3f p = new Point3f(-vn.getX(), vn.getY(), vn.getZ());
        p.scale(SCALE);
        return p;
    }

    protected abstract Appearance appearanceFromHealthPercentage(float pct);
    public abstract void updateHealth(float pct);

    protected Color3f getColorFromHealth(float pct) {
        Color color = Color.getHSBColor(
                SystemStatusSlider.getEmptyHue(true) - (SystemStatusSlider.getEmptyHue(true) - SystemStatusSlider.getFullHue(true)) * pct, 1, 1);
        return new Color3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

}
