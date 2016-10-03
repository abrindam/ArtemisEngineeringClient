package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class RadioButtonGroup extends TransparentJPanel {
	private static final long serialVersionUID = 1L;
	
	private List<RadioButton> buttons;
	private RadioButton selected = null;
	
	private static BufferedImage imageChecked, imageUnchecked;
	
	private Color backgroundColor, backgroundColorHover, labelColor, borderColor;
	
	public RadioButtonGroup(String[] labels, Runnable[] callbacks,
			int width, int button_height, int button_spacer,
			Color backgroundColor, Color backgroundColorHover, Color labelColor, Color borderColor) {	
		this.backgroundColor = backgroundColor;
		this.backgroundColorHover = backgroundColorHover;
		this.labelColor = labelColor;
		this.borderColor = borderColor;
		
		loadIcons();
		
		this.setLayout(null);
		this.setSize(width, (button_height + button_spacer) * (labels.length - 1) + button_height + 1);
		
		this.buttons = new ArrayList<RadioButton>(labels.length);
		for (int i = 0; i < labels.length; i++) {
			String label = labels[i];
	    	RadioButton rb = new RadioButton(label, this, callbacks[i],
	    			this.backgroundColor, this.backgroundColorHover, this.labelColor, this.borderColor,
	    			width, button_height);
	    	
	    	this.buttons.add(rb);
	    	this.add(rb).setLocation(0, i * (button_height + button_spacer));
		}
	}
	
    protected void loadIcons() {
        try {
            if (imageChecked == null) {
            	imageChecked = ImageIO.read(new File(System.getProperty("user.dir"), "assets/art/textures/gui/radiobutton/checked.png"));
            	imageUnchecked = ImageIO.read(new File(System.getProperty("user.dir"), "assets/art/textures/gui/radiobutton/unchecked.png"));
            }
        } catch (IOException e) {
            System.err.println("Unable to locate radio button icon(s)");
            e.printStackTrace(System.err);
            throw new RuntimeException();
        }
    }
    
    private void onChildSelected(RadioButton rb) {
    	if (rb.equals(this.selected))
    		return;
    	
    	for (RadioButton child : this.buttons) {
    		child.setSelected(false);
    	}
    	
    	this.selected = rb;
    	rb.setSelected(true);
    }
    
    public RadioButton getSelected() {
    	return this.selected;
    }
    
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D gfx = (Graphics2D) g;

		gfx.setColor(this.borderColor);
		gfx.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
	}


    /////////////////
    // RadioButton //
    /////////////////
    
	public class RadioButton extends Button {
		private static final long serialVersionUID = 1L;
		
		public RadioButtonGroup parent;
		private boolean selected = false;
		private int imageBorder = 2;
		
		public RadioButton(String label, RadioButtonGroup parent, Runnable onClickHandler,
							Color backgroundColor, Color backgroundColorHover, Color labelColor, Color borderColor,
							int minWidth, int minHeight) {
			super(label, onClickHandler, backgroundColor, backgroundColorHover, labelColor, borderColor, minWidth, minHeight);
			this.parent = parent;
			
			this.setMaximumSize(new Dimension(parent.getWidth(), parent.getHeight()));
			
			this.labelFont = new Font("Courier New", Font.BOLD, 18);
		}
		
		public void setSelected(boolean selected) {
			this.selected = selected;
			this.repaint();
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Graphics2D gfx = (Graphics2D) g;

			BufferedImage img = this.selected ? RadioButtonGroup.imageChecked : RadioButtonGroup.imageUnchecked; 
			float image_scale = ((float)this.getHeight() - 2f * (float)imageBorder) / (float)img.getHeight();			
			gfx.drawImage(img, imageBorder, imageBorder, (int)(image_scale * img.getWidth()), (int)(image_scale * img.getHeight()), this);
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			parent.onChildSelected(this);
			super.mouseClicked(e);
		}
	}
}