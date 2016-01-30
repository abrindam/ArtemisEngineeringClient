package com.brindyblitz.artemis.engconsole;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.brindyblitz.artemis.engconsole.ui.SystemStatusRenderer;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.protocol.core.eng.EngGridUpdatePacket.DamconStatus;
import net.dhleong.acl.util.GridCoord;

public class FakeEngineeringConsoleManager extends BaseEngineeringConsoleManager {
	private static final int MAX_COOLANT = 8;
	private Map<ShipSystem, Integer> energyAllocated = new HashMap<>();
	private Map<ShipSystem, Integer> coolantAllocated = new HashMap<>();
	private Map<ShipSystem, Integer> heat = new HashMap<>();
	private Map<GridCoord, Float> gridHealth = new HashMap<>();
	
	public FakeEngineeringConsoleManager() {
		for (ShipSystem system: ShipSystem.values()) {
			energyAllocated.put(system, 100);
			coolantAllocated.put(system, 0);
			heat.put(system, 0);
		}
		
		for (GridCoord gridCoord : this.getShipSystemGrid().getCoords()) {
			gridHealth.put(gridCoord, 1.0f);
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
		float maxHealth = 0f;
		float currentHealth = 0f;
		for (GridCoord gridCoord : this.getShipSystemGrid().getCoordsFor(system)) {
			maxHealth += 1.0;
			currentHealth += gridHealth.get(gridCoord);
		}
		
		return (int) (currentHealth/maxHealth * 100);
	}
	
	@Override
	public int getTotalCoolantRemaining() {
		return MAX_COOLANT - coolantAllocated.values().stream().mapToInt(Integer::intValue).sum();
	}
	
	@Override
	public Map<GridCoord, Float> getGridHealth() {
		return this.gridHealth;
	}
	
	

	@Override
	protected List<DamconStatus> getRawDamconStatus() {
		// return Arrays.asList(new DamconStatus(0, 6, 2, 2, 2, 2, 2, 3, 0.3f));
		return Arrays.asList(new DamconStatus(0, 6, 2, 0, 6, 2, 0, 6, 0f));
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
	public void moveDamconTeam(int teamId, GridCoord coord) {
		System.out.println("Moving DAMCON team " + teamId + " to grid " + coord);
		// Not supported for now		
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
					int newHeat = Math.max(0, Math.min(100, (int) (currentHeat + effectiveEnergy * 0.05)));
					if (newHeat == 100) {
						for (GridCoord gridCoord : getShipSystemGrid().getCoordsFor(system)) {
							if (gridHealth.get(gridCoord) != 0f) {
								gridHealth.put(gridCoord, 0f);
								newHeat = 50;
								break;
							}
						}
					}
					heat.put(system, newHeat);
					fireChange();						
				}
			}
		}
	}
}
