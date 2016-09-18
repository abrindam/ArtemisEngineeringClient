package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Button extends TransparentJPanel implements MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = 1L;
	
	private String label;
	private Color backgroundColor, backgroundColorHover, labelColor, borderColor; // TODO: ENHANCEMENT > on hover and on click colors, SFX
	private Font labelFont = new Font("Courier New", Font.BOLD, 12);
	private int width, height;
	private Runnable onClickHandler;
	
	private boolean hovered = false;
	
	public Button(String label, Runnable onClickHandler,
				  Color backgroundColor, Color backgroundColorHover, Color labelColor, Color borderColor,
				  int minWidth, int minHeight)
	{
		super();

		this.label = label;
		this.onClickHandler = onClickHandler;
		this.backgroundColor = backgroundColor;
		this.labelColor = labelColor;
		this.borderColor = borderColor;
		this.width = minWidth;
		this.height = minHeight;

		this.setSize(this.width + 1, this.height + 1);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		// TODO: ENHANCEMENT > auto-sizing buttons would be nice but the JPanel currently refuses to resize and none of these things worked
		// this.setMinimumSize(new Dimension(this.minWidth, this.minHeight));
		// this.setPreferredSize(new Dimension(width, height));
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D gfx = (Graphics2D) g;
		
		/* Calculate dimensions */
		g.setFont(this.labelFont);
		StringDimensions dim = this.measureString(this.label, gfx);
		/*int width = Math.max(this.minWidth, dim.getWidthInt());
		int height = Math.max(this.minHeight, dim.getHeightInt());
		this.setSize(width, height);*/
		
		/* Draw background */
		g.setColor(this.hovered ? this.backgroundColorHover : this.backgroundColor);
		g.fillRect(0, 0, width, height);
		
		/* Draw border */
		g.setColor(this.borderColor);
		g.drawRect(0, 0, width, height);
		
		/* Draw label */
		g.setColor(this.labelColor);
		g.drawString(this.label,
				(int)(width / 2f - dim.getWidthFloat() / 2f),
				(int)(height / 2f + dim.getHeight() / 2f));
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		this.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		this.onClickHandler.run();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		this.hovered = true;
		this.repaint();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		this.hovered = false;
		this.repaint();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
