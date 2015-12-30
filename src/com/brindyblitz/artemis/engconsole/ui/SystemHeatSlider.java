package com.brindyblitz.artemis.engconsole.ui;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import net.dhleong.acl.enums.ShipSystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SystemHeatSlider extends SystemStatusSlider {

    private static final long serialVersionUID = 1L;

    private static BufferedImage statusImageWithColor = null, statusImageWhite = null;

    public SystemHeatSlider(ShipSystem system, EngineeringConsoleManager engineeringConsoleManager) {
        super(system, engineeringConsoleManager);
    }

    @Override
    protected float getStatusScaleFactor() {
        return this.engineeringConsoleManager.getSystemHeat(this.system) / 100f;
    }

    @Override
    protected void loadIcons() {
        try {
            if (statusImageWithColor == null) {
                statusImageWithColor = ImageIO.read(new File(System.getProperty("user.dir"), "/art/heat_icon_with_color.png"));
                statusImageWhite = ImageIO.read(new File(System.getProperty("user.dir"), "/art/heat_icon_white.png"));
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
}
