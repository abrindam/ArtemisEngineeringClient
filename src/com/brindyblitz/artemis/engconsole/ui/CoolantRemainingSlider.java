package com.brindyblitz.artemis.engconsole.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.utils.AudioManager;

public class CoolantRemainingSlider extends TransparentJPanel implements MouseListener {

	private static final long serialVersionUID = 1L;

    private String label = "COOLANT";
	private static final Font LABEL_FONT = new Font("Courier New", Font.BOLD, 36);

    private static final int BUBBLE_DIMENSION = 32, EMPTY_BUBBLE_THICKNESS = 3;

    private static final Color color = new Color(0, 0, 255);

    private int width, height, bubbleX;

    private EngineeringConsoleManager engineeringConsoleManager;

    private AudioManager audioManager;

	public CoolantRemainingSlider(int width, int height, EngineeringConsoleManager engineeringConsoleManager, AudioManager audioManager) {
		this.engineeringConsoleManager = engineeringConsoleManager;
		this.audioManager = audioManager;
        this.width = width;
        this.height = height;

        this.setSize(this.width, this.height);
        this.bubbleX = this.width - (BUBBLE_DIMENSION + EMPTY_BUBBLE_THICKNESS);

        this.engineeringConsoleManager.getTotalShipCoolant().onChange(() -> this.repaint());
        this.engineeringConsoleManager.getTotalCoolantRemaining().onChange(() -> this.repaint());

        this.addMouseListener(this);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D gfx = (Graphics2D) g;
		drawSlider(gfx);
        drawLabel(gfx);
	}

	private void drawLabel(Graphics2D g) {	
		g.rotate(-Math.PI / 2f);
		g.setColor(Color.WHITE);
		g.setFont(LABEL_FONT);
		StringDimensions dim = this.measureString(this.label, g);
		g.drawString(label,
                -(height / 2f) - dim.getWidthFloat() / 2f,
                bubbleX - 8f);
        g.rotate(Math.PI / 2f);
	}

	private void drawSlider(Graphics2D g) {
		int total_coolant = this.engineeringConsoleManager.getTotalShipCoolant().get();
		if (total_coolant <= 0) {
			return;
		}

		int remaining_coolant = this.engineeringConsoleManager.getTotalCoolantRemaining().get();
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

	@Override
	public void mousePressed(MouseEvent e) {
		this.engineeringConsoleManager.resetCoolant();
		audioManager.playSound("beep.wav");
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}