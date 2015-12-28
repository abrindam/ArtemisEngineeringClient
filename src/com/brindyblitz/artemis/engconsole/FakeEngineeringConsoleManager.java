package com.brindyblitz.artemis.engconsole;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.brindyblitz.artemis.engconsole.ui.SystemStatusRenderer;

import net.dhleong.acl.enums.ShipSystem;

public class FakeEngineeringConsoleManager extends BaseEngineeringConsoleManager {

	private static final int MAX_COOLANT = 8;
	private Map<ShipSystem, Integer> energyAllocated = new HashMap<>();
	private Map<ShipSystem, Integer> coolantAllocated = new HashMap<>();
	private Map<ShipSystem, Integer> heat = new HashMap<>();
	
	public FakeEngineeringConsoleManager() {
		for (ShipSystem system: ShipSystem.values()) {
			energyAllocated.put(system, 100);
			coolantAllocated.put(system, 0);
			heat.put(system, 0);
		}
		
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new HeatAndDamageGenerator(), 0, 1, TimeUnit.SECONDS);
	}
	
	@Override
	public int getSystemEnergyAllocated(ShipSystem system) {
		return energyAllocated.get(system);
	}

	@Override
	public int getSystemCoolantAllocated(ShipSystem system) {
		return coolantAllocated.get(system);
	}
	
	@Override
	public int getSystemHeat(ShipSystem system) {
		return heat.get(system);
	}
	
	@Override
	public int getSystemHealth(ShipSystem system) {
		return 100;
	}
	
	@Override
	public int getTotalCoolantRemaining() {
		return MAX_COOLANT - coolantAllocated.values().stream().mapToInt(Integer::intValue).sum();
	}

	@Override
	protected void updateSystemEnergyAllocated(ShipSystem system, int amount) {
		energyAllocated.put(system, amount);
		fireChange();		
	}
	
	@Override
	protected void updateSystemCoolantAllocated(ShipSystem system, int amount) {
		coolantAllocated.put(system, amount);
		fireChange();
		
	}

	@Override
	public int getTotalShipCoolant() {
		return MAX_COOLANT;
	}
	
	public class HeatAndDamageGenerator implements Runnable {
		@Override
		public void run() {
			for (ShipSystem system: ShipSystem.values()) {
				int energyAllocated = getSystemEnergyAllocated(system);
				int energyCompensated = SystemStatusRenderer.getCooledEnergyThreshold(getSystemCoolantAllocated(system));
				int effectiveEnergy = energyAllocated - 100 - (energyCompensated - 100);
				
				if (effectiveEnergy != 0) {
					int currentHeat = getSystemHeat(system);
					heat.put(system, Math.max(0, Math.min(100, (int) (currentHeat + effectiveEnergy * 0.05))));
					fireChange();						
				}
			}
		}
	}
}
