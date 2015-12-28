package com.brindyblitz.artemis.engconsole;

import net.dhleong.acl.enums.ShipSystem;

public interface EngineeringConsoleManager {
	int getTotalShipCoolant();

	int getSystemEnergyAllocated(ShipSystem system);

	int getSystemCoolantAllocated(ShipSystem system);
	
	int getSystemHeat(ShipSystem system);
	
	int getSystemHealth(ShipSystem system);

	int getTotalCoolantRemaining();

	void incrementSystemEnergyAllocated(ShipSystem system, int amount);

	void incrementSystemCoolantAllocated(ShipSystem system, int amount);

	void addChangeListener(EngineeringConsoleChangeListener listener);
	
	public static interface EngineeringConsoleChangeListener {
		public void onChange();
	}

	void resetEnergy();

	void resetCoolant();
}