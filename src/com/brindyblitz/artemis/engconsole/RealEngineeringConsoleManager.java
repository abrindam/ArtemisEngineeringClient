package com.brindyblitz.artemis.engconsole;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.brindyblitz.artemis.protocol.NotifyingSystemManager.SystemManagerChangeListener;
import com.brindyblitz.artemis.protocol.WorldAwareRobustProxyListener;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.protocol.core.eng.EngSetCoolantPacket;
import net.dhleong.acl.protocol.core.eng.EngSetEnergyPacket;
import net.dhleong.acl.util.GridCoord;
import net.dhleong.acl.world.Artemis;

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
		this.worldAwareRobustProxyListener.getSystemManager().setSystemGrid(getShipSystemGrid());
	}
	
	@Override
	public int getSystemEnergyAllocated(ShipSystem system) {
		if (this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0) == null) {
			return 100;
		}
		return (int)(this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0).getSystemEnergy(system) * 300);
	}
	
	@Override
	public int getSystemCoolantAllocated(ShipSystem system) {
		if (this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0) == null) {
			return 0;
		}
		return this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0).getSystemCoolant(system);
	}
	
	@Override
	public int getSystemHeat(ShipSystem system) {
		if (this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0) == null) {
			return 0;
		}

		return (int) (this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0).getSystemHeat(system) * 100);
	}
	
	@Override
	public int getSystemHealth(ShipSystem system) {
		if (this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0) == null) {
			return 100;
		}
		return (int) (this.worldAwareRobustProxyListener.getSystemManager().getHealthOfSystem(system) * 100);
	}
	
	@Override
	public int getTotalCoolantRemaining() {
		if (this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0) == null) {
			return Artemis.DEFAULT_COOLANT;
		}
		final int totalCoolantUsed = Arrays.stream(ShipSystem.values()).mapToInt(system -> this.getSystemCoolantAllocated(system)).sum();
		return getTotalShipCoolant() - totalCoolantUsed;
	}

	@Override
	public int getTotalShipCoolant() {
		if (this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0) == null) {
			return 8;
		}
		return this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0).getAvailableCoolant();
	}
	
	@Override
	public Map<GridCoord, Float> getGridHealth() {
		Map<GridCoord, Float> result = new HashMap<>();
		for (Entry<GridCoord, Float> entry : this.worldAwareRobustProxyListener.getSystemManager().getGridDamages()) {
			result.put(entry.getKey(), 1.0f - entry.getValue());
		}
		return result;
	}
	
	@Override
	public void incrementSystemEnergyAllocated(ShipSystem system, int amount) {
		if (this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0) == null) {
			return;
		}
		super.incrementSystemEnergyAllocated(system, amount);
	}
	
	@Override
	public void incrementSystemCoolantAllocated(ShipSystem system, int amount) {
		if (this.worldAwareRobustProxyListener.getSystemManager().getPlayerShip(0) == null) {
			return;
		}
		super.incrementSystemCoolantAllocated(system, amount);
	}
	
	@Override
	protected void updateSystemEnergyAllocated(ShipSystem system, int amount) {
		this.worldAwareRobustProxyListener.getServer().send(new EngSetEnergyPacket(system, amount));		
	}
	
	@Override
	protected void updateSystemCoolantAllocated(ShipSystem system, int amount) {
		this.worldAwareRobustProxyListener.getServer().send(new EngSetCoolantPacket(system, amount));		
	}
}
