package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;

import javax.swing.JPanel;

public class TransparentJPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	public TransparentJPanel()
	{
		super();
		this.setOpaque(false);
		this.setBackground(new Color(0, 0, 0, 0));
	}
}
