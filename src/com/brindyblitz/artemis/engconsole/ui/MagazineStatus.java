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

public class MagazineStatus extends TransparentJPanel
{
	private static final long serialVersionUID = 1L;

	private EngineeringConsoleManager engineeringConsoleManager;
	
	private Map<OrdnanceType, JLabel> labels = new HashMap<OrdnanceType, JLabel>();
	
	private static final Font FONT = new Font("Courier New", Font.PLAIN, 14), TITLE_FONT = new Font("Courier New", Font.PLAIN, 14);
	private static final Color FONT_COLOR = Color.WHITE, BACKGROUND_COLOR = Color.BLACK, BORDER_COLOR = Color.WHITE;
	
	public MagazineStatus(EngineeringConsoleManager engineeringConsoleManager)
	{
		this.engineeringConsoleManager = engineeringConsoleManager;
		
		this.engineeringConsoleManager.getOrdnanceCount().onChange(() -> this.updateOrdnanceCounts());
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		@SuppressWarnings("unchecked")
		Map<TextAttribute, Integer> attributes = (Map<TextAttribute, Integer>)TITLE_FONT.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		JLabel title = new JLabel(" Magazine ");
		title.setBackground(BACKGROUND_COLOR);
		title.setForeground(FONT_COLOR);
		title.setFont(TITLE_FONT.deriveFont(attributes));
		this.add(title);
		
		for (OrdnanceType ot : OrdnanceType.values()) {
			JLabel label = new JLabel("0x " + ot);
			label.setBackground(BACKGROUND_COLOR);
			label.setForeground(FONT_COLOR);
			label.setFont(FONT);
			this.labels.put(ot, label);
			this.add(label);
		}
		
		this.setSize(new Dimension(200, 105)); //100));
		this.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		
		updateOrdnanceCounts();
	}
	
	private void updateOrdnanceCounts() {
		Map<OrdnanceType, Integer> magazine = this.engineeringConsoleManager.getOrdnanceCount().get();
		for (OrdnanceType ot : this.labels.keySet()) {
			JLabel label = this.labels.get(ot);
			label.setText("    " + magazine.get(ot) + "x " + ot);
		}
	}
}
