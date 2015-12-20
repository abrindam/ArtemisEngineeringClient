package com.brindybliz.artemis;

import net.dhleong.acl.enums.ShipSystem;

public interface EngineeringConsoleManager {

	int getSystemEnergyAllocated(ShipSystem system);

	int getSystemCoolantAllocated(ShipSystem system);

	void incrementSystemEnergyAllocated(ShipSystem system, int amount);

	void incrementSystemCoolantAllocated(ShipSystem system, int amount);

	void addChangeListener(EngineeringConsoleChangeListener listener);
	
	public static interface EngineeringConsoleChangeListener {
		public void onChange();
	}

}