package com.brindyblitz.artemis.engconsole.ui;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.walkertribe.ian.enums.ShipSystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SystemHeatSlider extends SystemStatusSlider {

    private static final long serialVersionUID = 1L;

    protected static final int
            WIDGET_HEIGHT = 20,
            WIDGET_WIDTH = 100,
            SLIDER_WIDTH = WIDGET_WIDTH / 2,
            SLIDER_HEIGHT = WIDGET_HEIGHT;

    private static BufferedImage statusImageWithColor = null, statusImageWhite = null;
    private int OVERHEAT_TOLERANCE_PCT = 25, CRITICAL_TOLERANCE_PCT = 75;
    
    private enum OverheatState { None, Overheated, Critical }
    private OverheatState overheatState = OverheatState.None;

    public SystemHeatSlider(ShipSystem system, EngineeringConsoleManager engineeringConsoleManager) {
        super(system, engineeringConsoleManager, WIDGET_WIDTH, WIDGET_HEIGHT, SLIDER_WIDTH, SLIDER_HEIGHT);
        this.engineeringConsoleManager.getSystemHeat().onChange(() -> this.repaint());
        this.engineeringConsoleManager.getSystemHeat().onChange(() -> this.onSystemHeatChange());
    }

    @Override
    protected int getStatusPctInt() {
        return this.engineeringConsoleManager.getSystemHeat().get().get(this.system);
    }

    @Override
    protected void loadIcons() {
        try {
            if (statusImageWithColor == null) {
                statusImageWithColor = ImageIO.read(new File(System.getProperty("user.dir"), "assets/art/textures/status/heat/color.png"));
                statusImageWhite = ImageIO.read(new File(System.getProperty("user.dir"), "assets/art/textures/status/heat/white.png"));
            }
        } catch (IOException e) {
            System.err.println("Unable to locate system status icon(s)");
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }

    @Override
    protected BufferedImage getStatusImageWithColor() {
        return statusImageWithColor;
    }

    @Override
    protected BufferedImage getStatusImageWhite() {
        return statusImageWhite;
    }

    @Override
    protected float getFullHue() {
        return 0f;
    }

    @Override
    protected float getEmptyHue() {
        return 60f / 360f;
    }
    
    private void onSystemHeatChange() {
    	if (this.engineeringConsoleManager.getSystemHealth().get().get(this.system) == 0)
    		return;
    	
    	int heat_pct = this.getStatusPctInt();
    	
    	switch (overheatState) {
    		case None:
    			if (heat_pct >= OVERHEAT_TOLERANCE_PCT) {
    				overheatState = OverheatState.Overheated;
    				this.engineeringConsoleManager.getAudioManager().playSound("alerts/overheat.wav");	
    			}
    			break;
    			
    		case Overheated:
    			if (heat_pct < OVERHEAT_TOLERANCE_PCT)
    				overheatState = OverheatState.None;
    			if (heat_pct >= CRITICAL_TOLERANCE_PCT) {
    				overheatState = OverheatState.Critical;
    				this.engineeringConsoleManager.getAudioManager().playSound("alerts/overheat_critical.wav");
    			}
    			break;
    			
    		case Critical:
    			if (heat_pct < CRITICAL_TOLERANCE_PCT)
    				overheatState = OverheatState.Overheated;
    			break;
    	}
    }
}
