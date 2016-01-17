package com.brindyblitz.artemis.engconsole.ui;

import java.awt.*;

import javax.swing.JPanel;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager.EngineeringConsoleChangeListener;
import net.dhleong.acl.world.Artemis;

public class CoolantRemainingSlider extends JPanel {

	private static final long serialVersionUID = 1L;

    private String label = "COOLANT";
	private static final Font LABEL_FONT = new Font("Courier New", Font.BOLD, 36);

    private static final int BUBBLE_DIMENSION = 32, EMPTY_BUBBLE_THICKNESS = 3;

    private static final Color color = new Color(0, 0, 255);

    private int width, height, bubbleX;

    private EngineeringConsoleManager engineeringConsoleManager;
	
	public CoolantRemainingSlider(EngineeringConsoleManager engineeringConsoleManager, int width, int height) {
		this.engineeringConsoleManager = engineeringConsoleManager;

        this.width = width;
        this.height = height;
        this.setSize(width, height);
        this.bubbleX = width - (BUBBLE_DIMENSION + EMPTY_BUBBLE_THICKNESS);

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
        drawLabel(gfx);
	}

	private void drawLabel(Graphics2D g) {
		g.rotate(-Math.PI / 2);
		g.setColor(Color.WHITE);
		g.setFont(LABEL_FONT);
		g.drawString(label,
                -(height / 2) - g.getFontMetrics().stringWidth(this.label) / 2,
                bubbleX - 8);
        g.rotate(Math.PI / 2);
	}

	private void drawSlider(Graphics2D g) {
		int total_coolant = this.engineeringConsoleManager.getTotalShipCoolant();
		if (total_coolant <= 0) {
			return;
		}

		int remaining_coolant = this.engineeringConsoleManager.getTotalCoolantRemaining();
        int used_coolant = total_coolant - remaining_coolant;
		int bubbleOffset = this.height / total_coolant;

		g.setColor(color);
		for (int i = total_coolant; i > used_coolant; i--) {
			g.fillOval(bubbleX, (i - 1) * bubbleOffset + EMPTY_BUBBLE_THICKNESS, BUBBLE_DIMENSION, BUBBLE_DIMENSION);
		}
        g.setStroke(new BasicStroke(EMPTY_BUBBLE_THICKNESS));
		for (int i = used_coolant; i > 0; i--) {
            g.drawOval(bubbleX, (i - 1) * bubbleOffset + EMPTY_BUBBLE_THICKNESS, BUBBLE_DIMENSION, BUBBLE_DIMENSION);
        }
	}
}