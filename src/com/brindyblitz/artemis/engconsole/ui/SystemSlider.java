package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JPanel;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager.EngineeringConsoleChangeListener;
import com.brindyblitz.artemis.engconsole.ui.SystemStatusRenderer.Interval;
import com.brindyblitz.artemis.engconsole.ui.SystemStatusRenderer.IntervalType;

import net.dhleong.acl.enums.ShipSystem;

public class SystemSlider extends JPanel implements KeyListener {

	private static final long serialVersionUID = 1L;
	private EngineeringConsoleManager engineeringConsoleManager;
	private SystemStatusRenderer systemStatusRenderer;
	private ShipSystem system;
	private int increaseKey, decreaseKey;
	private String label;

	private static final Font
		LABEL_FONT = new Font("Arial", Font.PLAIN, 16),
		SHORTCUT_FONT = new Font("Courier New", Font.BOLD | Font.ITALIC, 20);
	private static Color INCREASE_FONT_COLOR = Color.WHITE, DECREASE_FONT_COLOR = Color.WHITE;

	private static final int
		WIDGET_WIDTH = 100,
		SLIDER_WIDTH = WIDGET_WIDTH / 2,
		SLIDER_LEFT = SLIDER_WIDTH,
	
		SLIDER_HEIGHT = 300,
		WIDGET_HEIGHT = SLIDER_HEIGHT + 2 * SHORTCUT_FONT.getSize(),
		SLIDER_TOP = SHORTCUT_FONT.getSize(),
		SLIDER_BOTTOM = SLIDER_TOP + SLIDER_HEIGHT,
	
		SLIDER_MAX_PCT = 3,
		NOTCH_HEIGHT_FOR_100_PCTS = 4,
		NOTCH_HEIGHT_FOR_MINOR_PCTS = 2,
		NOTCH_PRECISION_LEVELS_PER_100_PCT = 4;
	private static final Color[] NOTCH_COLORS = new Color[]{Color.GREEN, new Color(255, 180, 0), Color.RED};

	public SystemSlider(ShipSystem system, String label, int increaseKey, int decreaseKey, EngineeringConsoleManager engineeringConsoleManager) {
		this.system = system;
		this.label = label;
		this.increaseKey = increaseKey;
		this.decreaseKey = decreaseKey;
		this.engineeringConsoleManager = engineeringConsoleManager;
		this.systemStatusRenderer = new SystemStatusRenderer(engineeringConsoleManager);

		this.setSize(WIDGET_WIDTH, WIDGET_HEIGHT);
		this.setBackground(new Color(0, 0, 0, 0));

		this.engineeringConsoleManager.addChangeListener(new EngineeringConsoleChangeListener() {

			@Override
			public void onChange() {
				SystemSlider.this.repaint();
			}
		});
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D gfx = (Graphics2D) g;
		drawSlider(gfx);
		drawLabel(gfx);
		drawShortcuts(gfx);
	}


	private void drawSlider(Graphics2D g) {
		/* Draw background */
		g.setColor(Color.WHITE);
		g.fillRect(SLIDER_LEFT, SLIDER_TOP, SLIDER_WIDTH, SLIDER_HEIGHT);

		/* Draw intervals */
		List<Interval> intervals = systemStatusRenderer.getSystemStatusAsIntervals(system);
		for (Interval interval : intervals) {
			g.setColor(this.getIntervalColor(interval.type));
			g.fillRect(SLIDER_LEFT, SLIDER_BOTTOM - interval.end, SLIDER_WIDTH, interval.end - interval.start); // TODO: TESTME
		}

		/* Draw level indicator marks */
		for (int i = 1; i <= SLIDER_MAX_PCT; i++) {
			g.setColor(NOTCH_COLORS[i - 1]);

			g.fillRect(SLIDER_LEFT,                                                                     // Draw 100%
					percentToY(i),
					SLIDER_WIDTH,
					NOTCH_HEIGHT_FOR_100_PCTS);
			drawNotchAndSubdivide(g, percentToY(i - 1), NOTCH_PRECISION_LEVELS_PER_100_PCT - 1, 0);     // Subdivide
		}
	}

	private static int percentToY(float percent) {
		return (int) (SLIDER_BOTTOM - (SLIDER_HEIGHT * (percent / (float) SLIDER_MAX_PCT)));
	}

	private void drawNotchAndSubdivide(Graphics2D g, int start_y, int max_level, int level) {
		float slider_section_height = (float) SLIDER_HEIGHT / (float) SLIDER_MAX_PCT;
		int dividers = (int) Math.pow(2, level);
		float height = slider_section_height / dividers;
		for (int i = 1; i < dividers; i++) {
			g.fillRect(SLIDER_LEFT,
					(int) (start_y - i * height),
					SLIDER_WIDTH / (level + 1),
					NOTCH_HEIGHT_FOR_MINOR_PCTS);
		}

		if (level < max_level) {
			drawNotchAndSubdivide(g, start_y, max_level, ++level);
		}
	}

	private void drawLabel(Graphics2D g) {
		g.rotate(-Math.PI / 2);
		g.setColor(Color.WHITE);
		g.setFont(LABEL_FONT);
		g.drawString(this.label.toUpperCase(),
				-SLIDER_BOTTOM + (SLIDER_HEIGHT / 2) - g.getFontMetrics().stringWidth(this.label.toUpperCase()) / 2,
				SLIDER_WIDTH - (LABEL_FONT.getSize() - 6));
		g.rotate(Math.PI / 2);
	}

	private void drawShortcuts(Graphics2D g) {
		g.setFont(SHORTCUT_FONT);

		g.setColor(INCREASE_FONT_COLOR);
		String increase = ("" + (char) this.increaseKey).toUpperCase();
		g.drawString(increase, SLIDER_WIDTH * 1.5f - g.getFontMetrics().stringWidth(increase) / 2f, SHORTCUT_FONT.getSize() - 5);

		g.setColor(DECREASE_FONT_COLOR);
		String decrease = ("" + (char) this.decreaseKey).toUpperCase();
		g.drawString(decrease, SLIDER_WIDTH * 1.5f - g.getFontMetrics().stringWidth(decrease) / 2f, SLIDER_BOTTOM + SHORTCUT_FONT.getSize() - 2);
	}

	private Color getIntervalColor(IntervalType type) {
		switch (type) {
		case OVERCHARGED_COOLED:
			return new Color(235, 195, 30);
		case OVERCHARGED_UNCOOLED:
			return Color.RED;
		case OVERCOOLED:
			return Color.CYAN;
		case UNDERCHARGED:
			return new Color(195, 30, 235);
		}
		throw new RuntimeException("Unexpected Interval Type");
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == this.increaseKey || e.getKeyCode() == this.decreaseKey) {
			if (e.isShiftDown()) {
				this.engineeringConsoleManager.incrementSystemCoolantAllocated(this.system, (e.getKeyCode() == this.increaseKey ? 1 : -1));
			} else {
				this.engineeringConsoleManager.incrementSystemEnergyAllocated(this.system, (e.getKeyCode() == this.increaseKey ? 30 : -30));
			}
			this.repaint();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
