package com.brindybliz.artemis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.JPanel;

import com.brindybliz.artemis.EngineeringConsoleManager.EngineeringConsoleChangeListener;
import com.brindybliz.artemis.SystemStatusRenderer.Interval;
import com.brindybliz.artemis.SystemStatusRenderer.IntervalType;

import net.dhleong.acl.enums.ShipSystem;

public class SystemSlider extends JPanel implements KeyListener {

	private static final long serialVersionUID = 1L;
	private EngineeringConsoleManager engineeringConsoleManager;
	private SystemStatusRenderer systemStatusRenderer;
	private ShipSystem system;
	private int increaseKey;
	private int decreaseKey;

	public SystemSlider(ShipSystem system, int increaseKey, int decreaseKey, EngineeringConsoleManager engineeringConsoleManager) {
		this.system = system;
		this.increaseKey = increaseKey;
		this.decreaseKey = decreaseKey;
		this.engineeringConsoleManager = engineeringConsoleManager;
		this.systemStatusRenderer = new SystemStatusRenderer(engineeringConsoleManager);
		
		this.setSize(100, 300);
		this.setBackground(Color.WHITE);
		
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
		drawSlider((Graphics2D) g);
		
	}
	
	private void drawSlider(Graphics2D g) {
		
		List<Interval> intervals = systemStatusRenderer.getSystemStatusAsIntervals(system);
		for (Interval interval: intervals) {
			g.setColor(this.getIntervalColor(interval.type));
			g.fillRect(0, 300 - interval.end, 100, interval.end - interval.start);
		}
		
		g.setColor(Color.GREEN);
		g.fillRect(0,  200 - 2, 100, 4);
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
			}
			else {
				this.engineeringConsoleManager.incrementSystemEnergyAllocated(this.system, (e.getKeyCode() == this.increaseKey ? 30 : -30));
			}
			this.repaint();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	
	
}
