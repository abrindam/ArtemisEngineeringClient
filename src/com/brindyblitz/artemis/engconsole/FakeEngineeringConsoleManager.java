package com.brindyblitz.artemis.engconsole;

import java.util.HashMap;
import java.util.Map;

import net.dhleong.acl.enums.ShipSystem;

public class FakeEngineeringConsoleManager extends BaseEngineeringConsoleManager {

	private static final int MAX_COOLANT = 8;
	private Map<ShipSystem, Integer> energyAllocated = new HashMap<>();
	private Map<ShipSystem, Integer> coolantAllocated = new HashMap<>();
	
	public FakeEngineeringConsoleManager() {
		for (ShipSystem system: ShipSystem.values()) {
			energyAllocated.put(system, 100);
			coolantAllocated.put(system, 0);
		}
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
}
