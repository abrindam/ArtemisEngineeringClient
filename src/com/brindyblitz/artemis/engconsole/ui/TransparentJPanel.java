package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class TransparentJPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	// TODO: REFACTOR > this should be detected exactly once, not for every panel
	private enum OS { OS_WINDOWS, OS_OSX, OS_OTHER }
	private OS operatingSystem; 
    protected int textHorizontalSpacer = 0;

	public TransparentJPanel() {
		super();
		
		this.setOpaque(false);
		this.setBackground(new Color(0, 0, 0, 0));
		
		determineOS();
	}
	
	private void determineOS() {
		String name = System.getProperty("os.name");
		if (isWindows(name))
		{
			this.textHorizontalSpacer = 4;
			this.operatingSystem = OS.OS_WINDOWS;
		}
		
		if (isOSX(name))
		{
			this.textHorizontalSpacer = -8;
			this.operatingSystem = OS.OS_OSX;
		}

		this.operatingSystem = OS.OS_OTHER;
	}
	
	private static boolean isWindows(String os_name) {
		return (os_name.startsWith("Windows"));
	}

	public static boolean isOSX(String os_name) {
		return (os_name.equals("Mac OS X"));
	}
	
	/**
	 * Measure string.  Be sure to set the font you want to use first.
	 * 
	 * @param s String to measure
	 * @param g Graphics2D context
	 * @return Dimensions of string
	 */
	public StringDimensions measureString(String s, Graphics2D g) {
		switch(this.operatingSystem) {
			case OS_WINDOWS:
				// getPixelBounds() produces less width on Windows than OSX for some reason (look and feel or native font differences?)
				Rectangle2D rec2d = g.getFontMetrics().getStringBounds(s, g); 
		        return new StringDimensions(rec2d.getWidth(), rec2d.getHeight());

			default:
		        // getFontMetrics().getStringBounds() produces unreliable height values on OSX for some reason
		        FontRenderContext frc = g.getFontRenderContext();
		        GlyphVector gv = g.getFont().createGlyphVector(frc, s);
		        Rectangle rec = gv.getPixelBounds(null, 0, 0);
		        return new StringDimensions(rec.getWidth(), rec.getHeight());
		}
	}
	
	protected class StringDimensions {
		private double width, height;
		
		public StringDimensions(double width, double height) {
			this.width = width;
			this.height = height;
		}
		
		public double getWidth() {
			return this.width;
		}
		
		public double getHeight() {
			return this.height;
		}
		
		public float getWidthFloat() {
			return (float)this.width;
		}
		
		public float getHeightFloat() {
			return (float)this.height;
		}
		
		public int getWidthInt() {
			return (int)Math.ceil(this.width);
		}
		
		public int getHeightInt() {
			return (int)Math.ceil(this.height);
		}
	}
}
