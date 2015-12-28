package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

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
		RED_HUE = 0 / 360f,
		YELLOW_HUE = 60 /360f;
	
	private static final Font
		LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
	
	private EngineeringConsoleManager engineeringConsoleManager;

	private ShipSystem system;
	
	public SystemHeatSlider(ShipSystem system, EngineeringConsoleManager engineeringConsoleManager) {
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
		
		g.setFont(LABEL_FONT);
		g.setColor(Color.WHITE);
		g.drawString("HEAT", SLIDER_WIDTH, LABEL_FONT.getSize());
		g.fillRect(SLIDER_LEFT, SLIDER_TOP, SLIDER_WIDTH, SLIDER_HEIGHT);
		float heatScaleFactor = this.engineeringConsoleManager.getSystemHeat(this.system) / 100f;
		g.setColor(Color.getHSBColor(YELLOW_HUE - (YELLOW_HUE - RED_HUE) * heatScaleFactor, 1, 1));
		g.fillRect(SLIDER_LEFT, SLIDER_TOP, (int) (SLIDER_WIDTH * heatScaleFactor), SLIDER_HEIGHT);
	}
}
