package com.brindyblitz.artemis.engconsole.config;

import java.util.HashMap;
import java.util.Map;

import net.dhleong.acl.enums.ShipSystem;

public class EnergyCoolantPreset {
	public static final int SAFE_ENERGY = -999;
	
	private Map<ShipSystem, Integer> energyPresets = new HashMap<>();
	private Map<ShipSystem, Integer> coolantPresets = new HashMap<>();
	private String name;
	
	public EnergyCoolantPreset(int energyDefault, int cooleantDefault) {
		for (ShipSystem shipSystem : ShipSystem.values()) {
			energyPresets.put(shipSystem, energyDefault);
			coolantPresets.put(shipSystem, cooleantDefault);
		}
	}
	
	public int getEnergyPreset(ShipSystem system) {
		return energyPresets.get(system);
	}
	
	public void setEnergyPreset(ShipSystem system, int energy) {
		energyPresets.put(system, energy);
	}
	
	public int getCoolantPreset(ShipSystem system) {
		return coolantPresets.get(system);
	}
	
	public void setCoolantPreset(ShipSystem system, int coolant) {
		coolantPresets.put(system, coolant);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;		
	}
	
}
