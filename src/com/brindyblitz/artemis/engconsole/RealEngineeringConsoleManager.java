package com.brindyblitz.artemis.engconsole;

import com.brindyblitz.artemis.protocol.NotifyingSystemManager.SystemManagerChangeListener;
import com.brindyblitz.artemis.protocol.WorldAwareRobustProxyListener;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.protocol.core.eng.EngSetCoolantPacket;
import net.dhleong.acl.protocol.core.eng.EngSetEnergyPacket;

public class RealEngineeringConsoleManager extends BaseEngineeringConsoleManager {

	private WorldAwareRobustProxyListener worldAwareRobustProxyListener;

	public RealEngineeringConsoleManager(WorldAwareRobustProxyListener worldAwareRobustProxyListener) {
		this.worldAwareRobustProxyListener = worldAwareRobustProxyListener;
		this.worldAwareRobustProxyListener.getSystemManager().addChangeListener(new SystemManagerChangeListener() {
			
			@Override
			public void onChange() {
				RealEngineeringConsoleManager.this.fireChange();
			}
		});
	}
	
	@Override
	public int getSystemEnergyAllocated(ShipSystem system) {
		if (this.worldAwareRobustProxyListener.getServer() == null) {
			return 100;
		}
		return (int)(this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0).getSystemEnergy(system) * 300);
	}
	
	@Override
	public int getSystemCoolantAllocated(ShipSystem system) {
		if (this.worldAwareRobustProxyListener.getServer() == null) {
			return 0;
		}
		return this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0).getSystemCoolant(system);
	}
	
	public int getTotalCoolantRemaining() {
		if (this.worldAwareRobustProxyListener.getServer() == null) {
			return 0;
		}
		return this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0).getAvailableCoolant();
	}
	
	@Override
	public void incrementSystemEnergyAllocated(ShipSystem system, int amount) {
		if (this.worldAwareRobustProxyListener.getServer() == null) {
			return;
		}
		this.worldAwareRobustProxyListener.getServer().send(new EngSetEnergyPacket(system, this.getSystemEnergyAllocated(system) + amount));
	}
	
	@Override
	public void incrementSystemCoolantAllocated(ShipSystem system, int amount) {
		if (this.worldAwareRobustProxyListener.getServer() == null) {
			return;
		}
		this.worldAwareRobustProxyListener.getServer().send(new EngSetCoolantPacket(system, Math.max(0, this.getSystemCoolantAllocated(system) + Math.min(amount, getTotalCoolantRemaining()))));
	}
}
