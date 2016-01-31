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
import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager.Events;

import net.dhleong.acl.enums.ShipSystem;

public abstract class SystemStatusSlider extends JPanel {
    private static final long serialVersionUID = 1L;

    private static Point statusImageDimensions = new Point(16, 16);

    protected Font statusFont = new Font("Courier New", Font.BOLD, 14);

    protected EngineeringConsoleManager engineeringConsoleManager;

    protected ShipSystem system;

    protected int widgetWidth, widgetHeight, sliderWidth, sliderHeight;

    public SystemStatusSlider(ShipSystem system, EngineeringConsoleManager engineeringConsoleManager,
                              int widget_width, int widget_height, int slider_width, int slider_height) {
        widgetWidth = widget_width;
        widgetHeight = widget_height;
        sliderWidth = slider_width;
        sliderHeight = slider_height;

        this.system = system;
        this.engineeringConsoleManager = engineeringConsoleManager;

        this.setSize(widget_width, widget_height);
        this.setBackground(new Color(0, 0, 0, 0));

        loadIcons();

        this.engineeringConsoleManager.onEvent(Events.CHANGE, () -> this.repaint());
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
        g.fillRect(sliderWidth, 0, sliderWidth, sliderHeight);

        int fill_width = (int)(sliderWidth * getStatusScaleFactor()),
                fill_right = sliderWidth + fill_width;

        g.setColor(getStatusColor());
        g.fillRect(sliderWidth, 0, fill_width, sliderHeight);

        int image_left = sliderWidth + sliderWidth / 2 - statusImageDimensions.x / 2,
                image_top = sliderHeight / 2 - statusImageDimensions.y / 2,
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
        g.drawString(status_pct_str, sliderWidth - (int) font_size.getWidth(), (int) (sliderHeight - sliderHeight / 2f + font_size.getHeight() / 4f));
    }

    private float getStatusScaleFactor() {
        return getStatusPctInt() / 100f;
    }

    protected Color getStatusColor() {
        float factor = getStatusScaleFactor();
        return factor == 0f ? Color.GRAY :
                Color.getHSBColor(getEmptyHue() - (getEmptyHue() - getFullHue()) * getStatusScaleFactor(), 1, 1);
    }

    protected abstract int getStatusPctInt();
    protected abstract void loadIcons();
    protected abstract BufferedImage getStatusImageWithColor();
    protected abstract BufferedImage getStatusImageWhite();

    protected abstract float getFullHue();
    protected abstract float getEmptyHue();
}
