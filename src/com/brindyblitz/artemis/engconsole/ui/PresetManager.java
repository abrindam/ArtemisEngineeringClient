package com.brindyblitz.artemis.engconsole.ui;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.config.ConfigurationLoader;
import com.brindyblitz.artemis.engconsole.config.EnergyCoolantPreset;

import com.walkertribe.ian.enums.ShipSystem;

public class PresetManager {
	private EnergyCoolantPreset[] presets;
	private EngineeringConsoleManager engineeringConsoleManager;

	public PresetManager(EngineeringConsoleManager engineeringConsoleManager) {
		this.engineeringConsoleManager = engineeringConsoleManager;
		presets = new ConfigurationLoader().getPresetConfiguration(100, 0);
	}
	
	public void applyPreset(int presetNumber) {
		EnergyCoolantPreset preset = presets[presetNumber];
		if (preset != null) {
			for (ShipSystem system : ShipSystem.values()) {
				engineeringConsoleManager.setSystemCoolantAllocated(system, preset.getCoolantPreset(system));
				if (preset.getEnergyPreset(system) == EnergyCoolantPreset.SAFE_ENERGY) {
					engineeringConsoleManager.setSystemEnergyAllocated(system, SystemStatusRenderer.getCooledEnergyThreshold(preset.getCoolantPreset(system)));
				}
				else {
					engineeringConsoleManager.setSystemEnergyAllocated(system, preset.getEnergyPreset(system));					
				}
			}
			
		}
	}
	
	public EnergyCoolantPreset[] getPresets() {
		return presets;
	}
}

