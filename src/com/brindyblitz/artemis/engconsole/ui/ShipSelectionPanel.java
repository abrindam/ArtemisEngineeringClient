package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager.Ship;

public class ShipSelectionPanel extends TransparentJPanel {
	private static final long serialVersionUID = 1L;

	EngineeringConsoleManager engineeringConsoleManager;
	
	private RadioButtonGroup radioButtonGroup;
	
	private Color backgroundColor = Color.BLACK,
			backgroundColorHover = Color.GRAY,
			labelColor = Color.WHITE,
			borderColor = Color.WHITE;
	
	public ShipSelectionPanel(EngineeringConsoleManager engineeringConsoleManager, int width, int height) {
		this.engineeringConsoleManager = engineeringConsoleManager;
		
		this.setVisible(false);
		this.setBounds(0, 0, width, height);
		this.setBackground(Color.BLACK);
	
		Button disconnect_button = new Button("Disconnect", new Runnable()
		{
			@Override
			public void run() { engineeringConsoleManager.disconnect(); }
		}, Color.BLACK, Color.RED, Color.WHITE, Color.WHITE, 80, 20);
		this.add(disconnect_button).setLocation(this.getWidth() - disconnect_button.getWidth() - 10, 5);
		
		Button ready_button = new Button("Ready To Play", new Runnable()
		{
			@Override
			public void run() { engineeringConsoleManager.ready(); }
		}, Color.BLACK, Color.GREEN, Color.WHITE, Color.WHITE, 120, 20);
		this.add(ready_button).setLocation(this.getWidth() - ready_button.getWidth() - 10, this.getHeight() - ready_button.getHeight() - 40);
		
		this.engineeringConsoleManager.getAllShips().onChange(() -> this.setShips());
	}
	
	private void setShips() {
		Ship[] ships = this.engineeringConsoleManager.getAllShips().get();
		
		String[] ship_names = new String[ships.length];
		Runnable[] callbacks = new Runnable[ships.length];
		
		for (int i = 0; i < ships.length; i++) {
			Ship ship = ships[i];
			ship_names[i] = "Ship " + ship.getShipNumber() + ": " + ship.getName();
			
			callbacks[i] = new Runnable() {
				@Override
				public void run() {
					engineeringConsoleManager.selectShip(ship.getShipNumber());
				}
			};
		}
		
		this.radioButtonGroup = new RadioButtonGroup(ship_names, callbacks,
				260, 30, 5, 
				this.backgroundColor, this.backgroundColorHover, this.labelColor, this.borderColor);

		this.add(this.radioButtonGroup).setLocation(100, 100);
		
		this.repaint();
	}
}
