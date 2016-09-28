package com.brindyblitz.artemis.engconsole;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.brindyblitz.artemis.engconsole.ui.SystemStatusRenderer;
import com.brindyblitz.artemis.utils.AudioManager;
import com.brindyblitz.artemis.utils.newton.DerivedProperty;
import com.brindyblitz.artemis.utils.newton.Property;
import com.brindyblitz.artemis.utils.newton.SettableProperty;

import com.walkertribe.ian.enums.OrdnanceType;
import com.walkertribe.ian.enums.ShipSystem;
import com.walkertribe.ian.protocol.core.eng.EngGridUpdatePacket.DamconStatus;
import com.walkertribe.ian.util.GridCoord;

public class FakeEngineeringConsoleManager extends BaseEngineeringConsoleManager {
	private static final int MAX_COOLANT = 8;
	
	private static final Map<ShipSystem, Integer> DEFAULT_ENERGY_ALLOCATED = new HashMap<>();
	private static final Map<ShipSystem, Integer> DEFAULT_COOLANT_ALLOCATED = new HashMap<>();
	private static final Map<ShipSystem, Integer> DEFAULT_HEAT = new HashMap<>();
	private static final Map<OrdnanceType, Integer> DEFAULT_ORDNANCE_COUNT = new HashMap<>();
	
	private AudioManager audioManager;
	
	static {
		for (ShipSystem system: ShipSystem.values()) {
			DEFAULT_ENERGY_ALLOCATED.put(system, 100);
			DEFAULT_COOLANT_ALLOCATED.put(system, 0);
			DEFAULT_HEAT.put(system, 0);
		}
		DEFAULT_ORDNANCE_COUNT.put(OrdnanceType.EMP, 4);
		DEFAULT_ORDNANCE_COUNT.put(OrdnanceType.NUKE, 2);
		DEFAULT_ORDNANCE_COUNT.put(OrdnanceType.HOMING, 8);
		DEFAULT_ORDNANCE_COUNT.put(OrdnanceType.PSHOCK, 2);
		DEFAULT_ORDNANCE_COUNT.put(OrdnanceType.MINE, 6);
	}
	
	public FakeEngineeringConsoleManager() {
		
		Map<GridCoord, Float> gridHealth = new HashMap<>();
		for (GridCoord gridCoord : this.getShipSystemGrid().getCoords()) {
			gridHealth.put(gridCoord, 1.0f);
		}
		this.gridHealth.set(gridHealth);
		
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new HeatAndDamageGenerator(), 0, 1, TimeUnit.SECONDS);
		Executors.newSingleThreadScheduledExecutor().schedule(() -> {
			this.gameState.set(GameState.INGAME);
		}, 2, TimeUnit.SECONDS);
	}
	
	
	@Override
	public void connect(String host) {
		//intentionally do nothing		
	}
	
	@Override
	public void connect(String host, int port) {
		//intentionally do nothing
	}
	
	@Override
	public void disconnect() {
		//intentionally do nothing
	}
	
	
	@Override
	public Property<GameState> getGameState() {
		return gameState;
	}
	private final SettableProperty<GameState> gameState = new SettableProperty<>(GameState.PREGAME);
	
	
	@Override
	public Property<Map<ShipSystem, Integer>> getSystemEnergyAllocated() {
		return systemEnergyAllocated;
	}
	private final SettableProperty<Map<ShipSystem, Integer>> systemEnergyAllocated = new SettableProperty<>(DEFAULT_ENERGY_ALLOCATED);
	
	@Override
	public Property<Map<ShipSystem, Integer>> getSystemCoolantAllocated() {
		return systemCoolantAllocated;
	}
	private final SettableProperty<Map<ShipSystem, Integer>> systemCoolantAllocated = new SettableProperty<>(DEFAULT_COOLANT_ALLOCATED);
	
	@Override
	public Property<Map<ShipSystem, Integer>> getSystemHeat() {
		return systemHeat;
	}
	private final SettableProperty<Map<ShipSystem, Integer>> systemHeat = new SettableProperty<>(DEFAULT_HEAT);
	
	@Override
	public Property<Map<GridCoord, Float>> getGridHealth() {
		return gridHealth;
	}
	private final SettableProperty<Map<GridCoord, Float>> gridHealth = new SettableProperty<>(new HashMap<>());
	
	@Override
	public Property<Map<ShipSystem, Integer>> getSystemHealth() {
		return systemHealth;
	}
	private final DerivedProperty<Map<ShipSystem, Integer>> systemHealth = new DerivedProperty<>( () -> {
		
		Map<ShipSystem, Integer> result = new HashMap<>();
		for(ShipSystem system: ShipSystem.values()) {
			float maxHealth = 0f;
			float currentHealth = 0f;
			for (GridCoord gridCoord : this.getShipSystemGrid().getCoordsFor(system)) {
				maxHealth += 1.0;
				Float gridCoordHealth = gridHealth.get().get(gridCoord);
				currentHealth += (gridCoordHealth != null) ? gridCoordHealth : 1.0f;
			}
			
			result.put(system, (int) (currentHealth/maxHealth * 100));
		}
		
		return result;
	}, gridHealth);

	@Override
	public Property<Integer> getTotalShipCoolant() {
		return totalShipCoolant;
	}
	private final SettableProperty<Integer> totalShipCoolant =  new SettableProperty<>(MAX_COOLANT);	
	
	@Override
	public Property<Integer> getTotalCoolantRemaining() {
		return totalCoolantRemaining;
	}
	private final DerivedProperty<Integer> totalCoolantRemaining = new DerivedProperty<>( () -> {
		return totalShipCoolant.get() - systemCoolantAllocated.get().values().stream().mapToInt(Integer::intValue).sum();
	}, systemCoolantAllocated, totalShipCoolant);
	
	@Override
	protected Property<List<DamconStatus>> getRawDamconStatus() {
		return damconStatus;
	}
	private final SettableProperty<List<DamconStatus>> damconStatus = new SettableProperty<>(Arrays.asList(new DamconStatus(0, 6, 2, 0, 6, 2, 0, 6, 0f)));
	
	@Override
	public Property<Float> getTotalEnergyRemaining() {
		return totalEnergyRemaining;
	}
	private final SettableProperty<Float> totalEnergyRemaining = new SettableProperty<>(1000f);
	
	@Override
	public Property<Integer> getFrontShieldStrength() {
		return frontShieldStrength;
	}
	private final SettableProperty<Integer> frontShieldStrength = new SettableProperty<>(80);
	
	@Override
	public Property<Integer> getRearShieldStrength() {
		return rearShieldStrength;
	}
	private final SettableProperty<Integer> rearShieldStrength = new SettableProperty<>(60);
	
	@Override
	public Property<Integer> getFrontShieldMaxStrength() {
		return frontShieldMaxStrength;
	}
	private final SettableProperty<Integer> frontShieldMaxStrength = new SettableProperty<>(80);
	
	@Override
	public Property<Integer> getRearShieldMaxStrength() {
		return rearShieldMaxStrength;
	}
	private final SettableProperty<Integer> rearShieldMaxStrength = new SettableProperty<>(80);
	
	@Override
	public Property<Boolean> getShieldsActive() {
		return shieldsActive;
	}
	private final SettableProperty<Boolean> shieldsActive = new SettableProperty<>(false);
	
	@Override
	public Property<Map<OrdnanceType, Integer>> getOrdnanceCount() {
		return ordnanceCount;
	}
	private final SettableProperty<Map<OrdnanceType, Integer>> ordnanceCount = new SettableProperty<>(DEFAULT_ORDNANCE_COUNT);
	
	@Override
	public Property<Boolean> getAutoDamcon() {
		return autoDamcon;
	}
	private final SettableProperty<Boolean> autoDamcon = new SettableProperty<>(true);
	
	public Property<Boolean> getWeaponsLocked() {
		return weaponsLocked;
	}
	private final SettableProperty<Boolean> weaponsLocked = new SettableProperty<>(false);

	@Override
	public Property<Boolean> getAutoBeams() {
		return autoBeams;
	}
	private final SettableProperty<Boolean> autoBeams = new SettableProperty<>(true);
	
	@Override
	protected void updateSystemEnergyAllocated(ShipSystem system, int amount) {
		Map<ShipSystem, Integer> energyAllocated = new HashMap<>(FakeEngineeringConsoleManager.this.systemEnergyAllocated.get());
		energyAllocated.put(system, amount);
		systemEnergyAllocated.set(energyAllocated);
	}
	
	@Override
	protected void updateSystemCoolantAllocated(ShipSystem system, int amount) {
		Map<ShipSystem, Integer> coolantAllocated = new HashMap<>(FakeEngineeringConsoleManager.this.systemCoolantAllocated.get());
		coolantAllocated.put(system, amount);
		systemCoolantAllocated.set(coolantAllocated);
		
	}
	
	@Override
	public void setAutoDamcon(boolean autoDamcon) {
		this.autoDamcon.set(autoDamcon);		
	}
	
	@Override
	public void moveDamconTeam(int teamId, GridCoord coord) {
		System.out.println("Moving DAMCON team " + teamId + " to grid " + coord);
		// Not supported for now		
	}
	
	public class HeatAndDamageGenerator implements Runnable {
		@Override
		public void run() {
			Map<ShipSystem, Integer> heat = new HashMap<>(FakeEngineeringConsoleManager.this.systemHeat.get());
			Map<GridCoord, Float> gridHealth = new HashMap<>(FakeEngineeringConsoleManager.this.gridHealth.get());
			for (ShipSystem system: ShipSystem.values()) {
				int energyAllocated = getSystemEnergyAllocated().get().get(system);
				int energyCompensated = SystemStatusRenderer.getCooledEnergyThreshold(getSystemCoolantAllocated().get().get(system));
				int effectiveEnergy = energyAllocated - 100 - (energyCompensated - 100);
				
				if (effectiveEnergy != 0) {
					int currentHeat = getSystemHeat().get().get(system);
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
				}
			}
			FakeEngineeringConsoleManager.this.systemHeat.set(heat);
			FakeEngineeringConsoleManager.this.gridHealth.set(gridHealth);
		}
	}

	@Override
	public AudioManager getAudioManager()
	{
		return this.audioManager;
	}


	@Override
	public void setAudioManager(AudioManager audioManager)
	{
		this.audioManager = audioManager;
	}
}
