package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.swing.JPanel;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.config.InputMapping;
import com.brindyblitz.artemis.engconsole.ui.SystemStatusRenderer.Interval;
import com.brindyblitz.artemis.engconsole.ui.SystemStatusRenderer.IntervalType;
import com.brindyblitz.artemis.utils.AudioManager;

import com.walkertribe.ian.enums.ShipSystem;
import com.walkertribe.ian.world.Artemis;

public class SystemSlider extends TransparentJPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	private EngineeringConsoleManager engineeringConsoleManager;
	private SystemStatusRenderer systemStatusRenderer;
	private ShipSystem system;
	private String label;
	private InputMapping inputMapping;

    private long lastScrollTime = System.currentTimeMillis();

	private static final Color
            SLIDER_BACKGROUND = Color.BLACK,
            DIVIDER = Color.LIGHT_GRAY,
            NOT_APPLICABLE = Color.DARK_GRAY;

	private static final Font
		LABEL_FONT = new Font("Courier New", Font.BOLD, 24),
		SHORTCUT_FONT = new Font("Courier New", Font.BOLD | Font.ITALIC, 20);
	private static Color INCREASE_FONT_COLOR = Color.WHITE, DECREASE_FONT_COLOR = Color.WHITE;

	private static final int
		ENERGY_INCREMENT = 25,
        COOLANT_INCREMENT = 1,

        // TODO: FILE ISSUE > consider adding this to configuration options.  It's not just a super-power-user option.  Mice vary
        // a lot on scroll rates and wheel dynamics, and smart trackpads (e.g. those on MacBooks) can act as scroll
        // devices as well.  50 feels too slow for my mouse wheel and too fast for my trackpad.
        // ~Jake
        SCROLL_TIMEOUT_MS = 50,

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

		SLIDER_MOUSE_TOP_SPACER = 3,

        SHORTCUT_MAX_LENGTH = 4;

	private static final Color[] NOTCH_COLORS = new Color[]{Color.GREEN, new Color(255, 180, 0), Color.RED};

	public SystemSlider(ShipSystem system, String label, InputMapping input_mapping,
						EngineeringConsoleManager engineeringConsoleManager) {
		this.system = system;
		this.label = label;
		this.inputMapping = input_mapping;
		this.engineeringConsoleManager = engineeringConsoleManager;
		this.systemStatusRenderer = new SystemStatusRenderer(engineeringConsoleManager);

		this.setSize(WIDGET_WIDTH, WIDGET_HEIGHT);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);

        this.engineeringConsoleManager.getSystemEnergyAllocated().onChange(() -> this.repaint());
        this.engineeringConsoleManager.getSystemCoolantAllocated().onChange(() -> this.repaint());
	}

	//////////
	// Draw //
	//////////

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
        int coolant_start_y = percentToY(SystemStatusRenderer.getCooledEnergyThreshold(this.engineeringConsoleManager.getSystemCoolantAllocated().get().get(this.system)) / 100f);
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
		String s = this.label.toUpperCase();		
		g.setColor(Color.WHITE);
		g.setFont(LABEL_FONT);
		StringDimensions dim = this.measureString(s, g);
		g.rotate(-Math.PI / 2f);
		{
			// Render at uniform first letter alignment height:
			// g.drawString(this.label.toUpperCase(), -SLIDER_BOTTOM + (SLIDER_HEIGHT / 2f) - dim.getWidthFloat() / 2f, SLIDER_WIDTH - 7f);
			
			// Render centered at middle
			g.drawString(this.label.toUpperCase(), -SLIDER_BOTTOM + (SLIDER_HEIGHT / 2f) - dim.getWidthFloat() / 2f, SLIDER_WIDTH - 7f);
		}
		g.rotate(Math.PI / 2f);
	}

	private void drawShortcuts(Graphics2D g) {
		g.setFont(SHORTCUT_FONT);

		g.setColor(INCREASE_FONT_COLOR);
		String increase = (this.inputMapping.increaseKeyStr).toUpperCase().substring(0, Math.min(SHORTCUT_MAX_LENGTH, this.inputMapping.increaseKeyStr.length()));
		StringDimensions increase_dim = this.measureString(increase, g);
		g.drawString(increase,
                SLIDER_WIDTH * 1.5f - increase_dim.getWidthFloat() / 2f,
                SHORTCUT_FONT.getSize() - 5f);

		g.setColor(DECREASE_FONT_COLOR);
        String decrease = (this.inputMapping.decreaseKeyStr).toUpperCase().substring(0, Math.min(SHORTCUT_MAX_LENGTH, this.inputMapping.decreaseKeyStr.length()));
        StringDimensions decrease_dim = this.measureString(increase, g);
		g.drawString(decrease,
                SLIDER_WIDTH * 1.5f - decrease_dim.getWidthFloat() / 2f,
                SLIDER_BOTTOM + SHORTCUT_FONT.getSize() - 2f);
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
			return new Color(90, 215, 255);
		case OVERCHARGED_UNCOOLED:
			return new Color(255, 0, 0);
		case OVERCOOLED:
			return new Color(0, 0, 255);
		case UNDERCHARGED:
			return new Color(0, 255, 200);
		}
		throw new RuntimeException("Unexpected Interval Type");
	}

	///////////
	// Input //
	///////////

    private void handleInput(boolean positive, boolean shift_down) {
    	this.engineeringConsoleManager.getAudioManager().playSound("beep.wav");

        if (shift_down) {
            this.engineeringConsoleManager.incrementSystemCoolantAllocated(this.system, positive ? COOLANT_INCREMENT : -COOLANT_INCREMENT);
        } else {
            this.engineeringConsoleManager.incrementSystemEnergyAllocated(this.system, positive ? ENERGY_INCREMENT : -ENERGY_INCREMENT);
        }

        this.repaint();
    }

    /***
     * Only one Swing item seems to be able to receive keys at once probably due to the insane Java focus
     * model (see https://docs.oracle.com/javase/7/docs/api/java/awt/doc-files/FocusSpec.html).
     *
     * As such, the UserInterfaceFrame redirects keys to relevant receivers that would normally implement
     * KeyListener.
     */
	public void handleKeyPress(KeyEvent e) {
		if (e.getKeyCode() == this.inputMapping.increaseKey || e.getKeyCode() == this.inputMapping.decreaseKey) {
            handleInput(e.getKeyCode() == this.inputMapping.increaseKey, e.isShiftDown());
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
        long t = System.currentTimeMillis();
        if (t - this.lastScrollTime > SCROLL_TIMEOUT_MS) {
            handleInput(e.getPreciseWheelRotation() < 0d, e.isShiftDown());
            this.lastScrollTime = t;
        }
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Note: pressed seems to work better than clicked (due to lag?)

		// Make coordinates relative to slider and check bounds
		int x = e.getX() - SLIDER_LEFT, y = e.getY() - (SLIDER_TOP + SLIDER_MOUSE_TOP_SPACER);
		if (x < 0 || y < 0 || x > WIDGET_WIDTH || y > SLIDER_HEIGHT) {
			return;
		}

		if (x < POWER_WIDTH) {				// Set power directly...
			int power = SLIDER_HEIGHT - y;	// Note: this relies on keeping the height of this widget equal to Artemis.MAX_ENERGY_ALLOCATION_PERCENT
			this.engineeringConsoleManager.setSystemEnergyAllocated(this.system, power);
			this.engineeringConsoleManager.getAudioManager().playSound("beep.wav");
		} else {							// Set coolant...
			int coolant_relative_y = percentToY(1f) - e.getY();			// get y coordinate of mouse relative to bottom of 1-coolant block
			int current_coolant_allocated = this.engineeringConsoleManager.getSystemCoolantAllocated().get().get(this.system);

			if (coolant_relative_y < 0) {								// click under 1-coolant block: set coolant to 0
				if (current_coolant_allocated != 0) {
					this.engineeringConsoleManager.setSystemCoolantAllocated(this.system, 0);
					this.engineeringConsoleManager.getAudioManager().playSound("beep.wav");
				}
			} else {
				for (int i = Artemis.MAX_COOLANT_PER_SYSTEM; i > 0; i--) {	// from the max-coolant block, find which block's bottom is below the mouse
					int bottom_range_y = percentToY(1f) - (percentToY(SystemStatusRenderer.getCooledEnergyThreshold(i - 1) / 100f) + SLIDER_MOUSE_TOP_SPACER + NOTCH_HEIGHT_FOR_MINOR_PCTS);

					if (coolant_relative_y > bottom_range_y) {				// if current block's bottom is below mouse, i.e. if mouse is inside this block
						if (current_coolant_allocated != i) {				// ignore setting equal to current setting
							int current_total_coolant_remaining = this.engineeringConsoleManager.getTotalCoolantRemaining().get();

							// if increasing coolant allocated to this system...
							if (i > current_coolant_allocated && i - current_coolant_allocated > current_total_coolant_remaining) {
								int clamped_coolant = current_total_coolant_remaining + current_coolant_allocated;

								if (current_total_coolant_remaining == 0) {									// coolant not changing because there's no more coolant to allocate (i.e. clamped_coolant = current_coolant_allocated)
									// TODO: AUDIO > play error sound here (coolant value not changing)
								} else  if (clamped_coolant > 0) {
									// set coolant to remaining + amount currently allocated to this system
									this.engineeringConsoleManager.setSystemCoolantAllocated(this.system, current_total_coolant_remaining + current_coolant_allocated);
									this.engineeringConsoleManager.getAudioManager().playSound("beep.wav");
								} else {
									// TODO: AUDIO > play error sound here (coolant depleted)
								}
							} else {
								this.engineeringConsoleManager.setSystemCoolantAllocated(this.system, i);		// set to requested coolant level
								this.engineeringConsoleManager.getAudioManager().playSound("beep.wav");
							}
						} else {
							// TODO: AUDIO > play error sound here (coolant value not changing)
						}
						break;
					}
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}
}