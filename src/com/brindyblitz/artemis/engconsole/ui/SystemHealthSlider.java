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

public class SystemHealthSlider extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final int
		WIDGET_HEIGHT = 40,
		WIDGET_WIDTH = 100,
		SLIDER_WIDTH = WIDGET_WIDTH / 2,
		SLIDER_TOP = 20,
		SLIDER_LEFT = SLIDER_WIDTH,
		SLIDER_HEIGHT = WIDGET_HEIGHT - SLIDER_TOP;

	private static final float
        GREEN_HUE = 120f / 360f,
		RED_HUE = 0f / 360f;

	private static BufferedImage healthImageRed = null, healthImageWhite = null;
    private static Point healthImageDimensions = new Point(16, 16);

	private EngineeringConsoleManager engineeringConsoleManager;

	private ShipSystem system;

	public SystemHealthSlider(ShipSystem system, EngineeringConsoleManager engineeringConsoleManager) {
        try {
            if (healthImageRed == null) {
                healthImageRed = ImageIO.read(new File(System.getProperty("user.dir"), "/art/health_icon_red.png"));
                healthImageWhite = ImageIO.read(new File(System.getProperty("user.dir"), "/art/health_icon_white.png"));
            }
        } catch (IOException e) {
            System.err.println("Unable to locate system health icon(s)");
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
				SystemHealthSlider.this.repaint();
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

        float healthScaleFactor = this.engineeringConsoleManager.getSystemHealth(this.system) / 100f;

        int fill_width = (int)(SLIDER_WIDTH * healthScaleFactor),
            fill_right = SLIDER_LEFT + fill_width;

        g.setColor(Color.getHSBColor(RED_HUE - (RED_HUE - GREEN_HUE) * healthScaleFactor, 1, 1));
        g.fillRect(SLIDER_LEFT, SLIDER_TOP, fill_width, SLIDER_HEIGHT);

        int image_left = SLIDER_LEFT + SLIDER_WIDTH / 2 - healthImageDimensions.x / 2,
            image_top = SLIDER_TOP + SLIDER_HEIGHT / 2 - healthImageDimensions.y / 2,
            image_right = image_left + healthImageDimensions.x;

        if (fill_right < image_left) {
            g.drawImage(healthImageRed, image_left, image_top, healthImageDimensions.x, healthImageDimensions.y, this);
        } else if (fill_right > image_right) {
            g.drawImage(healthImageWhite, image_left, image_top, healthImageDimensions.x, healthImageDimensions.y, this);
        } else {
            float left_pct = (fill_right - image_left) / (float) healthImageDimensions.x,
                  right_pct = 1f - left_pct;
            int left_image_width = (int)(healthImageWhite.getWidth() * left_pct),
                right_image_width = healthImageRed.getWidth() - left_image_width;

            if (left_image_width > 0) {
                BufferedImage left_subimage = healthImageWhite.getSubimage(0, 0, left_image_width, healthImageWhite.getHeight());
                g.drawImage(left_subimage, image_left, image_top, (int)(healthImageDimensions.x * left_pct), healthImageDimensions.y, this);
            }

            if (right_image_width > 0) {
                BufferedImage right_subimage = healthImageRed.getSubimage((int)(healthImageRed.getWidth() * left_pct), 0, (int)(healthImageRed.getWidth() * right_pct), healthImageRed.getHeight());
                g.drawImage(right_subimage, fill_right, image_top, (int)(healthImageDimensions.x * right_pct), healthImageDimensions.y, this);
            }
        }
	}
}
