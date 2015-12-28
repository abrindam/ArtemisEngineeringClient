package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager.EngineeringConsoleChangeListener;

public class CoolantRemainingSlider extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final Font
		LABEL_FONT = new Font("Arial", Font.PLAIN, 14);

	private static final int
		WIDGET_WIDTH = 600,
		WIDGET_HEIGHT = 50;
	
	private EngineeringConsoleManager engineeringConsoleManager;
	
	public CoolantRemainingSlider(EngineeringConsoleManager engineeringConsoleManager) {
		this.engineeringConsoleManager = engineeringConsoleManager;
		
		this.setSize(WIDGET_WIDTH, WIDGET_HEIGHT);
		this.setBackground(new Color(0, 0, 0, 0));

		this.engineeringConsoleManager.addChangeListener(new EngineeringConsoleChangeListener() {

			@Override
			public void onChange() {
				CoolantRemainingSlider.this.repaint();
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
		g.setFont(LABEL_FONT);
		g.drawString("COOLANT REMAINING", 0, 10);
		
		g.setColor(Color.CYAN);
		for (int i = 0; i < this.engineeringConsoleManager.getTotalCoolantRemaining(); i++) {
			g.fillOval(i * 25, 15, 20, 20);
		}
	}
}
