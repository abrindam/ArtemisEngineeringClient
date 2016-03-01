package com.brindyblitz.artemis.engconsole.ui;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import net.dhleong.acl.enums.ShipSystem;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class ShieldSlider extends SystemStatusSlider {

	private static final long serialVersionUID = 1L;

    protected BufferedImage statusImageWithColor, statusImageWhite;

    protected static final int
            WIDGET_HEIGHT = 20,
            WIDGET_WIDTH = 400,
            SLIDER_WIDTH = WIDGET_WIDTH / 2,
            SLIDER_HEIGHT = WIDGET_HEIGHT;

    private static final Color SHIELD_COLOR = new Color(0, 0, 255);

    public ShieldSlider(EngineeringConsoleManager engineeringConsoleManager) {
        super(null, engineeringConsoleManager, WIDGET_WIDTH, WIDGET_HEIGHT, SLIDER_WIDTH, SLIDER_HEIGHT);

        this.statusImageDimensions = new Point(128, 16);
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
    protected Color getStatusColor() {
        return SHIELD_COLOR;
    }

    @Override
    protected float getFullHue() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected float getEmptyHue() {
        throw new UnsupportedOperationException();
    }
}
