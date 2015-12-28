package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;

import net.dhleong.acl.enums.ShipSystem;

public class UserInterfaceFrame extends JFrame implements KeyListener{

	private static final long serialVersionUID = 1L;
	private EngineeringConsoleManager engineeringConsoleManager;
	private int numSliders = 0;

	public UserInterfaceFrame(EngineeringConsoleManager engineeringConsoleManager) {
		this.engineeringConsoleManager = engineeringConsoleManager;
		setTitle("Artemis: Engineering Console (Client)");
		setSize(1024, 768);
		getContentPane().setBackground(Color.BLACK);
		setLayout(null);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addKeyListener(this);

		this.addSlider(ShipSystem.BEAMS, "Primary Beam", KeyEvent.VK_Q, KeyEvent.VK_A);
		this.addSlider(ShipSystem.TORPEDOES, "Torpedoes", KeyEvent.VK_W, KeyEvent.VK_S);
		this.addSlider(ShipSystem.SENSORS, "Sensors", KeyEvent.VK_E, KeyEvent.VK_D);
		this.addSlider(ShipSystem.MANEUVERING, "Maneuver", KeyEvent.VK_R, KeyEvent.VK_F);
		this.addSlider(ShipSystem.IMPULSE, "Impulse", KeyEvent.VK_T, KeyEvent.VK_G);
		this.addSlider(ShipSystem.WARP_JUMP_DRIVE, "Warp", KeyEvent.VK_Y, KeyEvent.VK_H);
		this.addSlider(ShipSystem.FORE_SHIELDS, "Front Shield", KeyEvent.VK_U, KeyEvent.VK_J);
		this.addSlider(ShipSystem.AFT_SHIELDS, "Rear Shield", KeyEvent.VK_I, KeyEvent.VK_K);
		
		this.add(new CoolantRemainingSlider(engineeringConsoleManager)).setLocation(50, 570);

	}

	private void addSlider(ShipSystem system, String label, int increaseKey, int decreaseKey) {
		SystemSlider slider = new SystemSlider(system, label, increaseKey, decreaseKey, this.engineeringConsoleManager);
		this.add(slider).setLocation(this.numSliders * 125 + 25, 200);
		this.addKeyListener(slider);
		this.numSliders ++;
	}


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {				

		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			System.out.println("\n\n\n\n\n\n\n\n");
			System.out.println("Beams: " + this.engineeringConsoleManager.getSystemEnergyAllocated(ShipSystem.BEAMS) + "%");
			System.out.println("Torpedoes: " + this.engineeringConsoleManager.getSystemEnergyAllocated(ShipSystem.TORPEDOES) + "%");
			System.out.println("Sensors: " + this.engineeringConsoleManager.getSystemEnergyAllocated(ShipSystem.SENSORS) + "%");
			System.out.println("Manuvering: " + this.engineeringConsoleManager.getSystemEnergyAllocated(ShipSystem.MANEUVERING) + "%");
			System.out.println("Impulse: " + this.engineeringConsoleManager.getSystemEnergyAllocated(ShipSystem.IMPULSE) + "%");
			System.out.println("Warp: " + this.engineeringConsoleManager.getSystemEnergyAllocated(ShipSystem.WARP_JUMP_DRIVE) + "%");
			System.out.println("Front Shields: " + this.engineeringConsoleManager.getSystemEnergyAllocated(ShipSystem.FORE_SHIELDS) + "%");
			System.out.println("Rear Shields: " + this.engineeringConsoleManager.getSystemEnergyAllocated(ShipSystem.AFT_SHIELDS) + "%");
		}

	}	

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}


}
