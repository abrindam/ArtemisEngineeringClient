package com.brindyblitz.artemis.engconsole;

import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.world.Artemis;

public abstract class BaseEngineeringConsoleManager implements EngineeringConsoleManager {

	private List<EngineeringConsoleChangeListener> listeners = new ArrayList<>();
	
	
	protected void fireChange() {
		for (EngineeringConsoleChangeListener listener: listeners) {
			listener.onChange();
		}
	}
	
	public void addChangeListener(EngineeringConsoleChangeListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public void incrementSystemEnergyAllocated(ShipSystem system, int amount) {
		updateSystemEnergyAllocated(system, Math.min(Artemis.MAX_ENERGY_ALLOCATION_PERCENT, Math.max(0,this.getSystemEnergyAllocated(system) + amount)));
	}
	
	protected abstract void updateSystemEnergyAllocated(ShipSystem system, int amount);
	
	@Override
	public void incrementSystemCoolantAllocated(ShipSystem system, int amount) {
		updateSystemCoolantAllocated(system, Math.max(0, this.getSystemCoolantAllocated(system) + Math.min(amount, getTotalCoolantRemaining())));
	}
	
	protected abstract void updateSystemCoolantAllocated(ShipSystem system, int amount);

}
