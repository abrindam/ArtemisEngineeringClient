package com.brindyblitz.artemis.engconsole.ui;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import net.dhleong.acl.enums.ShipSystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SystemHealthSlider extends SystemStatusSlider {

	private static final long serialVersionUID = 1L;

    private static BufferedImage statusImageWithColor = null, statusImageWhite = null;

    public SystemHealthSlider(ShipSystem system, EngineeringConsoleManager engineeringConsoleManager) {
        super(system, engineeringConsoleManager);
    }

    @Override
    protected int getStatusPctInt() {
        return this.engineeringConsoleManager.getSystemHealth(this.system);
    }

    @Override
    protected void loadIcons() {
        try {
            if (statusImageWithColor == null) {
                statusImageWithColor = ImageIO.read(new File(System.getProperty("user.dir"), "/art/health_icon_with_color.png"));
                statusImageWhite = ImageIO.read(new File(System.getProperty("user.dir"), "/art/health_icon_white.png"));
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
        return 120f / 360f;
    }

    @Override
    protected float getEmptyHue() {
        return 0f;
    }
}
