package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PreGamePanel extends TransparentJPanel {

	private static final long serialVersionUID = 1L;
	private static final Font LABEL_FONT = new Font("Courier New", Font.BOLD, 36);
	private JLabel waiting;

	public PreGamePanel(int width, int height) {
		this.setVisible(false);
		this.setBounds(0, 0, width, height);
		this.setLayout(null);
		this.setBackground(Color.BLACK);
		
		this.waiting = new JLabel("Waiting For Game To Start...");
		waiting.setForeground(Color.WHITE);
		waiting.setFont(LABEL_FONT);
		waiting.setHorizontalAlignment(SwingConstants.CENTER);
		waiting.setBounds(0, height/2 - 40, width, 50);
		this.add(waiting);
	}
}
