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
import com.brindyblitz.artemis.engconsole.config.InputMapping;
import com.brindyblitz.artemis.engconsole.ui.SystemStatusRenderer.Interval;
import com.brindyblitz.artemis.engconsole.ui.SystemStatusRenderer.IntervalType;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.world.Artemis;

public class SystemSlider extends JPanel implements KeyListener {

	private static final long serialVersionUID = 1L;
	private EngineeringConsoleManager engineeringConsoleManager;
	private SystemStatusRenderer systemStatusRenderer;
	private ShipSystem system;
	private String label;
	private InputMapping inputMapping;

	private static final Color
            SLIDER_BACKGROUND = Color.BLACK,
            DIVIDER = Color.LIGHT_GRAY,
            NOT_APPLICABLE = Color.DARK_GRAY;

	private static final Font
		LABEL_FONT = new Font("Arial", Font.PLAIN, 16),
		SHORTCUT_FONT = new Font("Courier New", Font.BOLD | Font.ITALIC, 20);
	private static Color INCREASE_FONT_COLOR = Color.WHITE, DECREASE_FONT_COLOR = Color.WHITE;

	private static final int
		ENERGY_INCREMENT = 25,

		WIDGET_WIDTH = 100,
		SLIDER_WIDTH = WIDGET_WIDTH / 2,
		SLIDER_LEFT = SLIDER_WIDTH,

        POWER_WIDTH = (int) (2f * (float)SLIDER_WIDTH / 3f),
        BAR_GAP_WIDTH = 1,
        COOLANT_LEFT = SLIDER_LEFT + POWER_WIDTH + BAR_GAP_WIDTH,
        COOLANT_WIDTH = SLIDER_WIDTH - (POWER_WIDTH + BAR_GAP_WIDTH),

		SLIDER_HEIGHT = Artemis.MAX_ENERGY_ALLOCATION_PERCENT,
		WIDGET_HEIGHT = SLIDER_HEIGHT + 2 * SHORTCUT_FONT.getSize(),
		SLIDER_TOP = SHORTCUT_FONT.getSize(),
		SLIDER_BOTTOM = SLIDER_TOP + SLIDER_HEIGHT,

		SLIDER_MAX_PCT = Artemis.MAX_ENERGY_ALLOCATION_PERCENT / 100,
		NOTCH_HEIGHT_FOR_100_PCTS = 4,
		NOTCH_HEIGHT_FOR_MINOR_PCTS = 2,
		NOTCH_PRECISION_LEVELS_PER_100_PCT = Artemis.MAX_ENERGY_ALLOCATION_PERCENT / 100,

        SHORTCUT_MAX_LENGTH = 4;

	private static final Color[] NOTCH_COLORS = new Color[]{Color.GREEN, new Color(255, 180, 0), Color.RED};

	public SystemSlider(ShipSystem system, String label, InputMapping input_mapping, EngineeringConsoleManager engineeringConsoleManager) {
		this.system = system;
		this.label = label;
		this.inputMapping = input_mapping;
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
		g.setColor(SLIDER_BACKGROUND);
		g.fillRect(SLIDER_LEFT, SLIDER_TOP, SLIDER_WIDTH, SLIDER_HEIGHT);

		/* Draw intervals */
		List<Interval> intervals = systemStatusRenderer.getSystemStatusAsIntervals(system);
		for (Interval interval : intervals) {
			g.setColor(this.getIntervalColor(interval.type));
			g.fillRect(SLIDER_LEFT, SLIDER_BOTTOM - interval.end, POWER_WIDTH, interval.end - interval.start);
		}

        /* Draw power level indicator marks */
        for (int i = 1; i <= SLIDER_MAX_PCT; i++) {
            Color color = NOTCH_COLORS[i - 1];
            g.setColor(color);
            int y = percentToY(i);
			drawAndFillRect(g, SLIDER_LEFT, y, POWER_WIDTH - 1, NOTCH_HEIGHT_FOR_100_PCTS, color, Color.BLACK);
            drawNotchAndSubdivide(g, color, percentToY(i - 0.5f), (int) ((float) SLIDER_HEIGHT / (float) SLIDER_MAX_PCT), NOTCH_PRECISION_LEVELS_PER_100_PCT - 1, 0);
        }

        /* Fill coolant bar */
        g.setColor(Color.BLUE);
        int coolant_start_y = percentToY(SystemStatusRenderer.getCooledEnergyThreshold(this.engineeringConsoleManager.getSystemCoolantAllocated(this.system)) / 100f);
        g.fillRect(COOLANT_LEFT, coolant_start_y, COOLANT_WIDTH, percentToY(1f) - coolant_start_y);

        /* Draw coolant level indicator marks */
        for (int i = 1; i <= Artemis.MAX_COOLANT_PER_SYSTEM; i++) {
            int coolant_notch_y = percentToY(SystemStatusRenderer.getCooledEnergyThreshold(i) / 100f);

            drawAndFillRect(g, COOLANT_LEFT, coolant_notch_y, COOLANT_WIDTH + 1, NOTCH_HEIGHT_FOR_MINOR_PCTS, Color.CYAN, Color.BLACK);
        }

        /* Gray out coolant section below 100% power allocation and above full coolant allocation */
        g.setColor(NOT_APPLICABLE);
        g.fillRect(COOLANT_LEFT, percentToY(1f), COOLANT_WIDTH, (int) (SLIDER_HEIGHT / (Artemis.MAX_ENERGY_ALLOCATION_PERCENT / 100f)));
        int coolant_full_y = percentToY(SystemStatusRenderer.getCooledEnergyThreshold(Artemis.MAX_COOLANT_PER_SYSTEM) / 100f);
        g.fillRect(COOLANT_LEFT, SLIDER_TOP, COOLANT_WIDTH, coolant_full_y - SLIDER_TOP);

        /* Draw divider */
        g.setColor(DIVIDER);
        g.fillRect(SLIDER_LEFT + POWER_WIDTH - 1, SLIDER_TOP, BAR_GAP_WIDTH + 1, SLIDER_HEIGHT);

        /* Draw border */
        g.setColor(Color.BLUE);
        g.drawRect(SLIDER_LEFT, SLIDER_TOP, SLIDER_WIDTH, SLIDER_HEIGHT);
	}

	private static int percentToY(float percent) {
		return (int) (SLIDER_BOTTOM - (SLIDER_HEIGHT * (percent / (float) SLIDER_MAX_PCT)));
	}

	private void drawNotchAndSubdivide(Graphics2D g, Color c, int section_middle_y, int level_height, int max_level, int level) {
        int x = SLIDER_LEFT, width = POWER_WIDTH / (level + 2);
		drawAndFillRect(g, x, section_middle_y, width, NOTCH_HEIGHT_FOR_MINOR_PCTS, c, Color.BLACK);

        if (level < max_level) {
            drawNotchAndSubdivide(g, c, (int)(section_middle_y - level_height / 4), level_height / 2, max_level, level + 1);
            drawNotchAndSubdivide(g, c, (int)(section_middle_y + level_height / 4), level_height / 2, max_level, level + 1);
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
		String increase = (this.inputMapping.increaseKeyStr).toUpperCase().substring(0, Math.min(SHORTCUT_MAX_LENGTH, this.inputMapping.increaseKeyStr.length()));
		g.drawString(increase,
                     SLIDER_WIDTH * 1.5f - g.getFontMetrics().stringWidth(increase) / 2f,
                     SHORTCUT_FONT.getSize() - 5);

		g.setColor(DECREASE_FONT_COLOR);
        String decrease = (this.inputMapping.decreaseKeyStr).toUpperCase().substring(0, Math.min(SHORTCUT_MAX_LENGTH, this.inputMapping.decreaseKeyStr.length()));
		g.drawString(decrease,
                     SLIDER_WIDTH * 1.5f - g.getFontMetrics().stringWidth(decrease) / 2f,
                     SLIDER_BOTTOM + SHORTCUT_FONT.getSize() - 2);
	}

	private static void drawAndFillRect(Graphics2D g, int x, int y, int width, int height, Color fill, Color border) {
		g.setColor(fill);
		g.fillRect(x, y, width, height);
		g.setColor(border);
		g.drawRect(x, y, width, height);
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
		if (e.getKeyCode() == this.inputMapping.increaseKey || e.getKeyCode() == this.inputMapping.decreaseKey) {
			if (e.isShiftDown()) {
				this.engineeringConsoleManager.incrementSystemCoolantAllocated(this.system, (e.getKeyCode() == this.inputMapping.increaseKey ? 1 : -1));
			} else {
				this.engineeringConsoleManager.incrementSystemEnergyAllocated(this.system, (e.getKeyCode() == this.inputMapping.increaseKey ? ENERGY_INCREMENT : -ENERGY_INCREMENT));
			}
			this.repaint();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}
