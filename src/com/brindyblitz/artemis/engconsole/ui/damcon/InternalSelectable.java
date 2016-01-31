package com.brindyblitz.artemis.engconsole.ui.damcon;

import com.brindyblitz.artemis.engconsole.ui.SystemStatusSlider;
import com.sun.j3d.utils.geometry.Sphere;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import java.awt.*;

public abstract class InternalSelectable extends Internal {
    protected static final float SHININESS = 0f;
    protected static final Color3f BLACK = new Color3f(0f, 0f, 0f), WHITE = new Color3f(1f, 1f, 1f);
    private static final float TRANSPARENT = 1f, OPAQUE = 0f;

    protected boolean hovered = false;
    protected float healthPct = 1f;

    protected float radius;
    protected Sphere sphere;
    protected BranchGroup branchGroup;

    protected Appearance appearanceFromHealthPercentage() {
        Appearance app = new Appearance();
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        app.setMaterial(new Material(BLACK, getColorFromHealth(this.healthPct), BLACK, BLACK, SHININESS));  // TODO: PERF > cache material and update on health change?

        TransparencyAttributes transparency;
        if (!this.visible()) {
            transparency = new TransparencyAttributes(TransparencyAttributes.NICEST, TRANSPARENT);
            // TODO: show some sort of indicator when hovering over non-system node (billboards would help with this)
        } else if (this.selected()) {
            transparency = new TransparencyAttributes(TransparencyAttributes.NICEST, OPAQUE);
            app.setMaterial(new Material(BLACK, getColorFromHealth(this.healthPct), WHITE, BLACK, SHININESS));
        } else if (this.hovered()) {
            transparency = new TransparencyAttributes(TransparencyAttributes.NICEST, OPAQUE);
        } else { // if (this.visible())
            transparency = new TransparencyAttributes(TransparencyAttributes.NICEST, alpha);
        }
        app.setTransparencyAttributes(transparency);
        // TODO: > bug here.  Transparency of something is jumping on first node selection after launch.  Investigate.
        // Same if I select a damcon team too, something in depth buffer/render order is changing with pick

        return app;
    }

    public void setSelected(boolean selected) { }

    protected boolean visible() {
        return true;
    }

    protected abstract boolean selected();

    protected boolean hovered() {
        return this.hovered;
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