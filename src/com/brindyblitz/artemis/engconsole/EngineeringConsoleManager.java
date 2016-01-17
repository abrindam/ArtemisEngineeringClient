package com.brindyblitz.artemis.engconsole;

import java.util.Map;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.util.GridCoord;

public interface EngineeringConsoleManager {
	int getTotalShipCoolant();

	int getSystemEnergyAllocated(ShipSystem system);

	int getSystemCoolantAllocated(ShipSystem system);
	
	int getSystemHeat(ShipSystem system);
	
	int getSystemHealth(ShipSystem system);

	int getTotalCoolantRemaining();
	
	Map<GridCoord, Float> getGridHealth();

	void setSystemEnergyAllocated(ShipSystem system, int amount);

	void setSystemCoolantAllocated(ShipSystem system, int amount);
	
	void incrementSystemEnergyAllocated(ShipSystem system, int amount);

	void incrementSystemCoolantAllocated(ShipSystem system, int amount);

	void addChangeListener(EngineeringConsoleChangeListener listener);
	
	public static interface EngineeringConsoleChangeListener {
		public void onChange();
	}

	void resetEnergy();

	void resetCoolant();
}