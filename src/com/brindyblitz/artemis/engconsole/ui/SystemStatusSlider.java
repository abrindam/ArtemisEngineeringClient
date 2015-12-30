package com.brindyblitz.artemis.engconsole.ui;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager.EngineeringConsoleChangeListener;
import net.dhleong.acl.enums.ShipSystem;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class SystemStatusSlider extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int
            WIDGET_HEIGHT = 20,
            WIDGET_WIDTH = 100,
            SLIDER_WIDTH = WIDGET_WIDTH / 2,
            SLIDER_TOP = 0, // 20,
            SLIDER_LEFT = SLIDER_WIDTH,
            SLIDER_HEIGHT = WIDGET_HEIGHT - SLIDER_TOP;

    private static Point statusImageDimensions = new Point(16, 16);

    protected EngineeringConsoleManager engineeringConsoleManager;

    protected ShipSystem system;

    public SystemStatusSlider(ShipSystem system, EngineeringConsoleManager engineeringConsoleManager) {
        this.system = system;
        this.engineeringConsoleManager = engineeringConsoleManager;

        this.setSize(WIDGET_WIDTH, WIDGET_HEIGHT);
        this.setBackground(new Color(0, 0, 0, 0));

        loadIcons();

        this.engineeringConsoleManager.addChangeListener(new EngineeringConsoleChangeListener() {
            @Override
            public void onChange() {
                SystemStatusSlider.this.repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D gfx = (Graphics2D) g;
        drawSlider(gfx);
    }

    private void drawSlider(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(SLIDER_LEFT, SLIDER_TOP, SLIDER_WIDTH, SLIDER_HEIGHT);

        float statusScaleFactor = getStatusScaleFactor();

        int fill_width = (int)(SLIDER_WIDTH * statusScaleFactor),
                fill_right = SLIDER_LEFT + fill_width;

        g.setColor(Color.getHSBColor(getEmptyHue() - (getEmptyHue() - getFullHue()) * statusScaleFactor, 1, 1));
        g.fillRect(SLIDER_LEFT, SLIDER_TOP, fill_width, SLIDER_HEIGHT);

        int image_left = SLIDER_LEFT + SLIDER_WIDTH / 2 - statusImageDimensions.x / 2,
                image_top = SLIDER_TOP + SLIDER_HEIGHT / 2 - statusImageDimensions.y / 2,
                image_right = image_left + statusImageDimensions.x;

        BufferedImage status_image_with_color = getStatusImageWithColor(), status_image_white = getStatusImageWhite();

        if (fill_right < image_left) {
            g.drawImage(status_image_with_color, image_left, image_top, statusImageDimensions.x, statusImageDimensions.y, this);
        } else if (fill_right > image_right) {
            g.drawImage(status_image_white, image_left, image_top, statusImageDimensions.x, statusImageDimensions.y, this);
        } else {
            float left_pct = (fill_right - image_left) / (float) statusImageDimensions.x,
                    right_pct = 1f - left_pct;
            int left_image_width = (int)(status_image_white.getWidth() * left_pct),
                    right_image_width = status_image_with_color.getWidth() - left_image_width;

            if (left_image_width > 0) {
                BufferedImage left_subimage = status_image_white.getSubimage(0, 0, left_image_width, status_image_white.getHeight());
                g.drawImage(left_subimage, image_left, image_top, (int)(statusImageDimensions.x * left_pct), statusImageDimensions.y, this);
            }

            if (right_image_width > 0) {
                BufferedImage right_subimage = status_image_with_color.getSubimage((int)(status_image_with_color.getWidth() * left_pct), 0, (int)(status_image_with_color.getWidth() * right_pct), status_image_with_color.getHeight());
                g.drawImage(right_subimage, fill_right, image_top, (int)(statusImageDimensions.x * right_pct), statusImageDimensions.y, this);
            }
        }
    }

    protected abstract float getStatusScaleFactor();
    protected abstract void loadIcons();
    protected abstract BufferedImage getStatusImageWithColor();
    protected abstract BufferedImage getStatusImageWhite();
    protected abstract float getFullHue();
    protected abstract float getEmptyHue();
}
