package com.brindyblitz.artemis.engconsole.ui;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import net.dhleong.acl.enums.ShipSystem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AftShieldSlider extends ShieldSlider {
	private static final long serialVersionUID = 1L;

	public AftShieldSlider(EngineeringConsoleManager engineeringConsoleManager) {
		super(engineeringConsoleManager);
		
		this.engineeringConsoleManager.getRearShieldStrength().onChange(() -> this.repaint());
		this.engineeringConsoleManager.getRearShieldMaxStrength().onChange(() -> this.repaint());
	}
	
    @Override
    protected int getStatusPctInt() {
    	return (int) (100 * ((float)this.engineeringConsoleManager.getRearShieldStrength().get() /
    						 (float)this.engineeringConsoleManager.getRearShieldMaxStrength().get()));	
    }
	
    @Override
    protected void loadIcons() {
        try {
            if (statusImageWithColor == null) {
                statusImageWithColor = ImageIO.read(new File(System.getProperty("user.dir"), "assets/art/textures/status/shield/aft/color.png"));
                statusImageWhite = ImageIO.read(new File(System.getProperty("user.dir"), "assets/art/textures/status/shield/aft/white.png"));
            }
        } catch (IOException e) {
            System.err.println("Unable to locate system status icon(s)");
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }
}