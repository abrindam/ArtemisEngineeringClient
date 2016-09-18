package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.brindyblitz.artemis.engconsole.config.EnergyCoolantPreset;

public class PresetPanel extends TransparentJPanel {

	private static final long serialVersionUID = 1L;
	private static final Font LABEL_FONT = new Font("Courier New", Font.BOLD, 14);
	private static final Font HEADER_FONT = new Font("Courier New", Font.BOLD, 24);
	
	private PresetManager presetManager;

	public PresetPanel(PresetManager presetManager) {
		this.presetManager = presetManager;
		this.setSize(200, 300);
		
//		this.setBackground(Color.RED);
		this.setBackground(new Color(0, 0, 0, 0));
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D gfx = (Graphics2D) g;
		gfx.setColor(Color.WHITE);
		gfx.setFont(HEADER_FONT);
		
		gfx.drawString("PRESETS", 0, 15);
		
		gfx.setColor(Color.GREEN);
		gfx.setFont(LABEL_FONT);
		
		int presetNumber = 0;
		for (EnergyCoolantPreset energyCoolantPreset : this.presetManager.getPresets()) {
			String label = presetNumber + " - ";
			if (energyCoolantPreset != null) {
				if (energyCoolantPreset.getName() == null) {
					label += "Preset #" + presetNumber;
				}
				else {
					label += energyCoolantPreset.getName(); 
				}
			}
			else {
				label += "(unset)";
			}
			int y = (presetNumber ==0 ? 10 : presetNumber) * 20 + 20;
			gfx.drawString(label, 0, y);
			presetNumber ++;
		}
	}
}
