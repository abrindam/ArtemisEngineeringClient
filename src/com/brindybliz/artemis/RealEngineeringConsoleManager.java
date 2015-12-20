package com.brindybliz.artemis;

import java.util.ArrayList;
import java.util.List;

import com.brindybliz.artemis.NotifyingSystemManager.SystemManagerChangeListener;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.protocol.core.eng.EngSetCoolantPacket;
import net.dhleong.acl.protocol.core.eng.EngSetEnergyPacket;

public class RealEngineeringConsoleManager implements EngineeringConsoleManager {

	private List<EngineeringConsoleChangeListener> listeners = new ArrayList<>();
	private WorldAwareRobustProxyListener worldAwareRobustProxyListener;

	public RealEngineeringConsoleManager(WorldAwareRobustProxyListener worldAwareRobustProxyListener) {
		this.worldAwareRobustProxyListener = worldAwareRobustProxyListener;
		this.worldAwareRobustProxyListener.getSystemManager().addChangeListener(new SystemManagerChangeListener() {
			
			@Override
			public void onChange() {
				for (EngineeringConsoleChangeListener listener: listeners) {
					listener.onChange();
				}
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
		return (int)(this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0).getSystemCoolant(system));
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
		this.worldAwareRobustProxyListener.getServer().send(new EngSetCoolantPacket(system, this.getSystemCoolantAllocated(system) + amount));
	}
	
	public void addChangeListener(EngineeringConsoleChangeListener listener) {
		this.listeners.add(listener);
	}
}
