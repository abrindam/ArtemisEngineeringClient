package com.brindyblitz.artemis.engconsole.ui.damcon;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager.EnhancedDamconStatus;
import com.brindyblitz.artemis.utils.AudioManager;

import javax.media.j3d.Appearance;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InternalTeam extends InternalSelectable {
    private EnhancedDamconStatus status;
    protected boolean selected = false;

    private static final String HUD_ICON = "hud/inner_diamond.png",
                                SELECTION_ICON = "hud/selection_diamond.png";
    private static final Color DAMCON_COLOR = new Color(230, 120, 0),
                               DAMCON_SELECTION_BOX_COLOR = new Color(255, 255, 255, 200);
    private static final int STANDARD_ALPHA = 175, HOVERED_ALPHA = 255;
    private static final float ICON_DIM = 0.075f;

    private Shape3D selectionShape;
    private Texture selectionVisibleTexture, selectionInvisibleTexture;

    public static Map<Integer, String> ON_ORDER_RESPONSES = new HashMap<>();

    public InternalTeam(EngineeringConsoleManager.EnhancedDamconStatus damcon_status, AudioManager audio_manager) {
        super(HUD_ICON, DAMCON_COLOR, STANDARD_ALPHA, HOVERED_ALPHA, ICON_DIM, audio_manager);

        this.status = damcon_status;

        QuadArray selection_billboard = createBillboard();
        BufferedImage selection_image = loadImage(SELECTION_ICON);
        this.selectionVisibleTexture = loadTexture(selection_image, DAMCON_SELECTION_BOX_COLOR);
        this.selectionInvisibleTexture = loadTexture(selection_image, INVISIBLE_COLOR);
        Appearance appearance = generateAppearance(this.selectionInvisibleTexture);
        this.selectionShape = new Shape3D(selection_billboard, appearance);
        this.transformGroup.addChild(this.selectionShape);

        // TODO: ENHANCEMENT > add team number to DAMCON icon?  Also, list team statuses somewhere?  Add team health bar
        // or member count?  At least show that on hover...
    }

    // TODO: API > DAMCON API needs to get health based on remaining / default team member counts?

    @Override
    public void setSelected(boolean selected) {
        if (selected) {
            System.out.println("Selecting " + this);
            audioManager.queueSound("voice/on_select/" + (this.getTeamID() + 1) + ".wav");

            // Use this if you want to hear all of the responses at random
            // int random = 1 + (int)(Math.random() * 9d);
            // audioManager.queueSound("voice/on_select/" + random + ".wav");
        }

        this.selected = selected;

        Appearance app = this.selectionShape.getAppearance();
        app.setTexture(selected ? selectionVisibleTexture : selectionInvisibleTexture);
    }

    @Override
    protected boolean selected() {
        return this.selected;
    }

    public int getTeamID() {
        return status.getTeamNumber();
    }

    @Override
    public String toString() {
        return this.status.toString();
    }
}