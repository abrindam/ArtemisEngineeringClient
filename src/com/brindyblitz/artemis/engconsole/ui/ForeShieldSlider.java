package com.brindyblitz.artemis.engconsole.ui;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;

public class ForeShieldSlider extends ShieldSlider {
	private static final long serialVersionUID = 1L;
	
	public ForeShieldSlider(EngineeringConsoleManager engineeringConsoleManager) {
		super(engineeringConsoleManager);
		
		this.engineeringConsoleManager.getFrontShieldStrength().onChange(() -> this.repaint());
		this.engineeringConsoleManager.getFrontShieldMaxStrength().onChange(() -> this.repaint());
	}
	
    @Override
    protected int getStatusPctInt() {
    	return (int) (100 * ((float)this.engineeringConsoleManager.getFrontShieldStrength().get() /
    						 (float)this.engineeringConsoleManager.getFrontShieldMaxStrength().get()));	
    }
	
    @Override
    protected void loadIcons() {
        try {
            if (statusImageWithColor == null) {
                statusImageWithColor = ImageIO.read(new File(System.getProperty("user.dir"), "assets/art/textures/status/shield/fore/color.png"));
                statusImageWhite = ImageIO.read(new File(System.getProperty("user.dir"), "assets/art/textures/status/shield/fore/white.png"));
            }
        } catch (IOException e) {
            System.err.println("Unable to locate system status icon(s)");
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }
}