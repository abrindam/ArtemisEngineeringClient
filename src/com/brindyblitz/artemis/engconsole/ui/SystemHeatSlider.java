package com.brindyblitz.artemis.engconsole.ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager.EngineeringConsoleChangeListener;

import net.dhleong.acl.enums.ShipSystem;

public class SystemHeatSlider extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int
		WIDGET_HEIGHT = 40,
		WIDGET_WIDTH = 100,
		SLIDER_WIDTH = WIDGET_WIDTH / 2,
		SLIDER_TOP = 20,
		SLIDER_LEFT = SLIDER_WIDTH,
		SLIDER_HEIGHT = WIDGET_HEIGHT - SLIDER_TOP;
	
	private static final float
		RED_HUE = 0f / 360f,
		YELLOW_HUE = 60f / 360f;

	private static BufferedImage heatImageOrange = null, heatImageWhite = null;
    private static Point heatImageDimensions = new Point(16, 16);

	private EngineeringConsoleManager engineeringConsoleManager;

	private ShipSystem system;
	
	public SystemHeatSlider(ShipSystem system, EngineeringConsoleManager engineeringConsoleManager) {
        try {
            if (heatImageOrange == null) {
                heatImageOrange = ImageIO.read(new File(System.getProperty("user.dir"), "/art/heat_icon_orange.png"));
                heatImageWhite = ImageIO.read(new File(System.getProperty("user.dir"), "/art/heat_icon_white.png"));
            }
        } catch (IOException e) {
            System.err.println("Unable to locate system heat icon(s)");
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }

		this.system = system;
		this.engineeringConsoleManager = engineeringConsoleManager;
		
		this.setSize(WIDGET_WIDTH, WIDGET_HEIGHT);
		this.setBackground(new Color(0, 0, 0, 0));

		this.engineeringConsoleManager.addChangeListener(new EngineeringConsoleChangeListener() {
			@Override
			public void onChange() {
				SystemHeatSlider.this.repaint();
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

        float heatScaleFactor = this.engineeringConsoleManager.getSystemHeat(this.system) / 100f;
        int fill_width = (int)(SLIDER_WIDTH * heatScaleFactor),
            fill_right = SLIDER_LEFT + fill_width;

        g.setColor(Color.getHSBColor(YELLOW_HUE - (YELLOW_HUE - RED_HUE) * heatScaleFactor, 1, 1));
        g.fillRect(SLIDER_LEFT, SLIDER_TOP, fill_width, SLIDER_HEIGHT);

        int image_left = SLIDER_LEFT + SLIDER_WIDTH / 2 - heatImageDimensions.x / 2,
            image_top = SLIDER_TOP + SLIDER_HEIGHT / 2 - heatImageDimensions.y / 2,
            image_right = image_left + heatImageDimensions.x;

        if (fill_right < image_left) {
            g.drawImage(heatImageOrange, image_left, image_top, heatImageDimensions.x, heatImageDimensions.y, this);
        } else if (fill_right > image_right) {
            g.drawImage(heatImageWhite, image_left, image_top, heatImageDimensions.x, heatImageDimensions.y, this);
        } else {
            float left_pct = (fill_right - image_left) / (float)heatImageDimensions.x,
                  right_pct = 1f - left_pct;
            int left_image_width = (int)(heatImageWhite.getWidth() * left_pct),
                right_image_width = heatImageOrange.getWidth() - left_image_width;

            if (left_image_width > 0) {
                BufferedImage left_subimage = heatImageWhite.getSubimage(0, 0, left_image_width, heatImageWhite.getHeight());
                g.drawImage(left_subimage, image_left, image_top, (int)(heatImageDimensions.x * left_pct), heatImageDimensions.y, this);
            }

            if (right_image_width > 0) {
                BufferedImage right_subimage = heatImageOrange.getSubimage((int)(heatImageOrange.getWidth() * left_pct), 0, (int)(heatImageOrange.getWidth() * right_pct), heatImageOrange.getHeight());
                g.drawImage(right_subimage, fill_right, image_top, (int)(heatImageDimensions.x * right_pct), heatImageDimensions.y, this);
            }
        }
	}
}
