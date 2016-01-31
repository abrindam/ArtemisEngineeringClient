package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;

import net.dhleong.acl.enums.ShipSystem;

public abstract class SystemStatusSlider extends JPanel {
    private static final long serialVersionUID = 1L;

    protected boolean isHealth;

    protected static final int
            WIDGET_HEIGHT = 20,
            WIDGET_WIDTH = 100,
            SLIDER_WIDTH = WIDGET_WIDTH / 2,
            SLIDER_TOP = 0,
            SLIDER_LEFT = SLIDER_WIDTH,
            SLIDER_HEIGHT = WIDGET_HEIGHT - SLIDER_TOP,
            SLIDER_BOTTOM = SLIDER_TOP + SLIDER_HEIGHT;

    private static Point statusImageDimensions = new Point(16, 16);

    protected Font statusFont = new Font("Courier New", Font.BOLD, 14);

    protected EngineeringConsoleManager engineeringConsoleManager;

    protected ShipSystem system;

    public SystemStatusSlider(ShipSystem system, EngineeringConsoleManager engineeringConsoleManager, boolean is_health) {
        this.system = system;
        this.engineeringConsoleManager = engineeringConsoleManager;
        this.isHealth = is_health;

        this.setSize(WIDGET_WIDTH, WIDGET_HEIGHT);
        this.setBackground(new Color(0, 0, 0, 0));

        loadIcons();

        this.engineeringConsoleManager.addChangeListener(() -> this.repaint());
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D gfx = (Graphics2D) g;
        drawSlider(gfx);
        drawStatusPctStr(gfx);
    }

    private void drawSlider(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(SLIDER_LEFT, SLIDER_TOP, SLIDER_WIDTH, SLIDER_HEIGHT);

        int fill_width = (int)(SLIDER_WIDTH * getStatusScaleFactor()),
                fill_right = SLIDER_LEFT + fill_width;

        g.setColor(getStatusColor());
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

    private void drawStatusPctStr(Graphics2D g) {
        g.setFont(statusFont);
        // g.setColor(Color.WHITE);
        g.setColor(getStatusColor());

        String status_pct_str = "" + getStatusPctInt() + "%";    // This won't pad zeroes
        // String status_pct_str = String.format("%03d%%", getStatusPctInt());

        // g.getFontMetrics().getStringBounds() produces unreliable height values for some reason
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector gv = g.getFont().createGlyphVector(frc, status_pct_str);
        Rectangle font_size = gv.getPixelBounds(null, 0, 0);

        // This should be dividing font height by 2f, not 4f, but for some reason everything is twice as tall as I expect.  SLIDER_HEIGHT is 20 but it's rendering as 40.  It makes no sense, but this works for now.
        g.drawString(status_pct_str, SLIDER_LEFT - (int) font_size.getWidth(), (int) (SLIDER_BOTTOM - SLIDER_HEIGHT / 2f + font_size.getHeight() / 4f));
    }

    private float getStatusScaleFactor() {
        return getStatusPctInt() / 100f;
    }

    private Color getStatusColor() {
        float factor = getStatusScaleFactor();
        return factor == 0f ? Color.GRAY :
                Color.getHSBColor(getEmptyHue(isHealth) - (getEmptyHue(isHealth) - getFullHue(isHealth)) * getStatusScaleFactor(), 1, 1);
    }

    protected abstract int getStatusPctInt();
    protected abstract void loadIcons();
    protected abstract BufferedImage getStatusImageWithColor();
    protected abstract BufferedImage getStatusImageWhite();

    public static float getFullHue(boolean health) {
        if (health) {
            return 120f / 360f;
        } else {
            return 0f;
        }
    }

    public static float getEmptyHue(boolean health) {
        if (health) {
            return 0f;
        } else {
            return 60f / 360f;
        }
    }
}
