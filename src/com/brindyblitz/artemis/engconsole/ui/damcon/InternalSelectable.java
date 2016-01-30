package com.brindyblitz.artemis.engconsole.ui.damcon;

import com.brindyblitz.artemis.engconsole.ui.SystemStatusSlider;
import com.sun.j3d.utils.geometry.Sphere;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import java.awt.*;

public abstract class InternalSelectable extends Internal {
    protected static final float RADIUS = 0.05f;
    protected static final float SHININESS = 0f;
    protected static final Color3f BLACK = new Color3f(0f, 0f, 0f);

    protected boolean selected = false, hovered = false;
    protected float healthPct = 1f;

    protected Sphere sphere;
    protected BranchGroup branchGroup;

    protected Appearance appearanceFromHealthPercentage() {
        Appearance app = new Appearance();
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        app.setMaterial(new Material(BLACK, getColorFromHealth(this.healthPct), BLACK, BLACK, SHININESS));
        // TODO: FILE ISSUE > when hovering, make opaque or circle or something

        if (this.visible()) {
            TransparencyAttributes transparency = new TransparencyAttributes(TransparencyAttributes.NICEST, 1f);
            app.setTransparencyAttributes(transparency);
        } else if (this.selected) {
            TransparencyAttributes ta = new TransparencyAttributes(TransparencyAttributes.NICEST, 0f);
            app.setTransparencyAttributes(ta);
        } else if (this.hovered) {
            TransparencyAttributes ta = new TransparencyAttributes(TransparencyAttributes.NICEST, 0f);
            app.setTransparencyAttributes(ta);
        } else {
            TransparencyAttributes transparency = new TransparencyAttributes(TransparencyAttributes.NICEST, alpha);
            app.setTransparencyAttributes(transparency);
        }
        // TODO: > bug here.  Transparency of something is jumping on first node selection after launch.  Investigate.
        // Same if I select a damcon team too, something in depth buffer/render order is changing with pick

        return app;
    }

    protected boolean visible() {
        return true;
    }

    protected void setPickable(Shape3D shape) {
        shape.setCapability(Shape3D.ENABLE_PICK_REPORTING);
        shape.setPickable(true);
    }

    public void updateHealth(float pct) {
        // Note: subclasses don't need to override this now but they will if I don't resolve the
        // genericization of sphere vs. other geometry used for their models
        this.healthPct = pct;
    }

    protected Color3f getColorFromHealth(float pct) {
        Color color = Color.getHSBColor(
                SystemStatusSlider.getEmptyHue(true) - (SystemStatusSlider.getEmptyHue(true) - SystemStatusSlider.getFullHue(true)) * pct, 1, 1);
        return new Color3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        this.sphere.setAppearance(appearanceFromHealthPercentage());
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
        this.sphere.setAppearance(appearanceFromHealthPercentage());
    }

    public BranchGroup getBranchGroup() {
        return branchGroup;
    }

    public Shape3D getShape() {
        return this.sphere.getShape(Sphere.BODY);
    }
}