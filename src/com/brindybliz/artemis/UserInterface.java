package com.brindybliz.artemis;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.protocol.core.eng.EngSetCoolantPacket;
import net.dhleong.acl.protocol.core.eng.EngSetEnergyPacket;

public class UserInterface extends JFrame implements KeyListener{

	private static final long serialVersionUID = 1L;
	private WorldAwareRobustProxyListener worldAwareRobustProxyListener;
	
	public UserInterface(WorldAwareRobustProxyListener worldAwareRobustProxyListener) {
		this.worldAwareRobustProxyListener = worldAwareRobustProxyListener;
		setTitle("Artemis Client");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        this.addKeyListener(this);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		handleKey(e, KeyEvent.VK_Q, ShipSystem.BEAMS, true);
		handleKey(e, KeyEvent.VK_A, ShipSystem.BEAMS, false);
		handleKey(e, KeyEvent.VK_W, ShipSystem.TORPEDOES, true);
		handleKey(e, KeyEvent.VK_S, ShipSystem.TORPEDOES, false);
		handleKey(e, KeyEvent.VK_E, ShipSystem.SENSORS, true);
		handleKey(e, KeyEvent.VK_D, ShipSystem.SENSORS, false);
		handleKey(e, KeyEvent.VK_R, ShipSystem.MANEUVERING, true);
		handleKey(e, KeyEvent.VK_F, ShipSystem.MANEUVERING, false);
		handleKey(e, KeyEvent.VK_T, ShipSystem.IMPULSE, true);
		handleKey(e, KeyEvent.VK_G, ShipSystem.IMPULSE, false);
		handleKey(e, KeyEvent.VK_Y, ShipSystem.WARP_JUMP_DRIVE, true);
		handleKey(e, KeyEvent.VK_H, ShipSystem.WARP_JUMP_DRIVE, false);
		handleKey(e, KeyEvent.VK_U, ShipSystem.FORE_SHIELDS, true);
		handleKey(e, KeyEvent.VK_J, ShipSystem.FORE_SHIELDS, false);
		handleKey(e, KeyEvent.VK_I, ShipSystem.AFT_SHIELDS, true);
		handleKey(e, KeyEvent.VK_K, ShipSystem.AFT_SHIELDS, false);
				
		
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			System.out.println("\n\n\n\n\n\n\n\n");
			System.out.println("Beams: " + this.getSystemEnergyAllocated(ShipSystem.BEAMS) + "%");
			System.out.println("Torpedoes: " + this.getSystemEnergyAllocated(ShipSystem.TORPEDOES) + "%");
			System.out.println("Sensors: " + this.getSystemEnergyAllocated(ShipSystem.SENSORS) + "%");
			System.out.println("Manuvering: " + this.getSystemEnergyAllocated(ShipSystem.MANEUVERING) + "%");
			System.out.println("Impulse: " + this.getSystemEnergyAllocated(ShipSystem.IMPULSE) + "%");
			System.out.println("Warp: " + this.getSystemEnergyAllocated(ShipSystem.WARP_JUMP_DRIVE) + "%");
			System.out.println("Front Shields: " + this.getSystemEnergyAllocated(ShipSystem.FORE_SHIELDS) + "%");
			System.out.println("Rear Shields: " + this.getSystemEnergyAllocated(ShipSystem.AFT_SHIELDS) + "%");
		}
		
	}
	
	private void handleKey(KeyEvent e, int targetKey, ShipSystem shipSystem, boolean postive) {
		if (e.getKeyCode() == targetKey) {
			if (e.isShiftDown()) {
				this.worldAwareRobustProxyListener.getServer().send(new EngSetCoolantPacket(shipSystem, this.getSystemCoolantAllocated(shipSystem) + (postive ? 1 : -1)));
			}
			else {
				this.worldAwareRobustProxyListener.getServer().send(new EngSetEnergyPacket(shipSystem, this.getSystemEnergyAllocated(shipSystem) + (postive ? 30 : -30)));
			}
			
		}
	}
	
	private int getSystemEnergyAllocated(ShipSystem system) {
		return (int)(this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0).getSystemEnergy(system) * 300);
	}
	
	private int getSystemCoolantAllocated(ShipSystem system) {
		return (int)(this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0).getSystemCoolant(system));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
