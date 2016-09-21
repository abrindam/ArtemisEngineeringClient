package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.walkertribe.ian.enums.OrdnanceType;

public class WeaponsStatus extends TransparentJPanel
{
	private static final long serialVersionUID = 1L;

	private EngineeringConsoleManager engineeringConsoleManager;
	
	private JLabel lockStatusLabel, autoBeamsLabel;
	
	private static final Font FONT = new Font("Courier New", Font.PLAIN, 14), TITLE_FONT = new Font("Courier New", Font.PLAIN, 14);
	private static final Color FONT_COLOR = Color.WHITE, BACKGROUND_COLOR = Color.BLACK, BORDER_COLOR = Color.WHITE;
	
	public WeaponsStatus(EngineeringConsoleManager engineeringConsoleManager)
	{
		this.engineeringConsoleManager = engineeringConsoleManager;
		
		this.engineeringConsoleManager.getWeaponsLocked().onChange(() -> new Runnable()
		{
			@Override
			public void run() {
				lockStatusLabel.setText("    Target: " + (engineeringConsoleManager.getWeaponsLocked().get() ? "Locked" : "-"));
			}
		});
		
		this.engineeringConsoleManager.getAutoBeams().onChange(() -> new Runnable()
		{
			@Override
			public void run() {
				autoBeamsLabel.setText("    Beams Mode: " + (engineeringConsoleManager.getAutoBeams().get() ? "Auto" : "Manual"));
			}
		});
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		@SuppressWarnings("unchecked")
		Map<TextAttribute, Integer> attributes = (Map<TextAttribute, Integer>)TITLE_FONT.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		JLabel title = new JLabel(" Weapons ");
		title.setBackground(BACKGROUND_COLOR);
		title.setForeground(FONT_COLOR);
		title.setFont(TITLE_FONT.deriveFont(attributes));
		this.add(title);
		
		this.lockStatusLabel = new JLabel("    Target: -");
		lockStatusLabel.setBackground(BACKGROUND_COLOR);
		lockStatusLabel.setForeground(FONT_COLOR);
		lockStatusLabel.setFont(FONT);
		this.add(lockStatusLabel);
		
		this.autoBeamsLabel = new JLabel("    Beams Mode: Auto");
		autoBeamsLabel.setBackground(BACKGROUND_COLOR);
		autoBeamsLabel.setForeground(FONT_COLOR);
		autoBeamsLabel.setFont(FONT);
		this.add(autoBeamsLabel);
		
		this.setSize(new Dimension(200, 55));
		this.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
	}
}
