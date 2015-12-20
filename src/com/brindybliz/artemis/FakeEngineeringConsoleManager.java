package com.brindybliz.artemis;

import java.util.HashMap;
import java.util.Map;

import net.dhleong.acl.enums.ShipSystem;

public class FakeEngineeringConsoleManager implements EngineeringConsoleManager {

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
	public void incrementSystemEnergyAllocated(ShipSystem system, int amount) {
		energyAllocated.put(system, getSystemEnergyAllocated(system) + amount);

	}

	@Override
	public void incrementSystemCoolantAllocated(ShipSystem system, int amount) {
		coolantAllocated.put(system, getSystemCoolantAllocated(system) + amount);
	}

	@Override
	public void addChangeListener(EngineeringConsoleChangeListener listener) {
		//do nothing
	}

}
