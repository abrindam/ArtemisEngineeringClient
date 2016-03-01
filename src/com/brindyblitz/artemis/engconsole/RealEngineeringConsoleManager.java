package com.brindyblitz.artemis.engconsole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.brindyblitz.artemis.protocol.NotifyingSystemManager;
import com.brindyblitz.artemis.protocol.WorldAwareRegularServer;
import com.brindyblitz.artemis.protocol.WorldAwareRobustProxyListener;
import com.brindyblitz.artemis.protocol.WorldAwareServer;
import com.brindyblitz.artemis.utils.newton.DerivedProperty;
import com.brindyblitz.artemis.utils.newton.ObservableAdapter;
import com.brindyblitz.artemis.utils.newton.Property;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.protocol.core.eng.EngGridUpdatePacket.DamconStatus;
import net.dhleong.acl.protocol.core.eng.EngSendDamconPacket;
import net.dhleong.acl.protocol.core.eng.EngSetCoolantPacket;
import net.dhleong.acl.protocol.core.eng.EngSetEnergyPacket;
import net.dhleong.acl.util.GridCoord;
import net.dhleong.acl.world.Artemis;

public class RealEngineeringConsoleManager extends BaseEngineeringConsoleManager {

	private WorldAwareServer worldAwareServer;
	private boolean proxy;
	private ObservableAdapter connectionStateChangeObservable = new ObservableAdapter();
	private ObservableAdapter systemManagerChangeObservable = new ObservableAdapter();

	public RealEngineeringConsoleManager(boolean proxy) {
		this.proxy = proxy;		
	}
	
	public void connect(String host) {
		this.connect(host, 2010);
	}
	
	public void connect(String host, int port) {
		if (proxy) {
			this.worldAwareServer = new WorldAwareRobustProxyListener(host, port, port);
		}
		else {
			try {
				this.worldAwareServer = new WorldAwareRegularServer(host, port);
			} catch (IOException e) {
				//abort without updating state
				return;
			}			
		}
		connectionStateChangeObservable.triggerChange();
		this.worldAwareServer.onEvent(WorldAwareServer.Events.CONNECTION_STATE_CHANGE, () -> connectionStateChangeObservable.triggerChange());
		this.worldAwareServer.getSystemManager().events.on(NotifyingSystemManager.Events.CHANGE, () -> systemManagerChangeObservable.triggerChange());
		this.worldAwareServer.getSystemManager().setSystemGrid(getShipSystemGrid());
		
	}
	
	@Override
	public Property<GameState> getGameState() {
		return gameState;
	}
	private final DerivedProperty<GameState> gameState = new DerivedProperty<>(() -> {
		if (worldAwareServer == null || !worldAwareServer.isConnected()) {
			return GameState.DISCONNECTED;
		}
		else if ( this.worldAwareServer.getSystemManager().getPlayerShip(0) != null) {
			return GameState.INGAME;
		}
		else {
			return GameState.PREGAME;
		}
	}, systemManagerChangeObservable, connectionStateChangeObservable);
	
	
	@Override
	public Property<Map<ShipSystem, Integer>> getSystemEnergyAllocated() {
		return systemEnergyAllocated;
	}
	private final DerivedProperty<Map<ShipSystem, Integer>> systemEnergyAllocated = new DerivedProperty<>( () -> {
		
		Map<ShipSystem, Integer> result = new HashMap<>();
		for(ShipSystem system: ShipSystem.values()) {
			if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
				result.put(system, 100);
			}
			else {
				result.put(system, (int)(this.worldAwareServer.getSystemManager().getPlayerShip(0).getSystemEnergy(system) * 300));							
			}
		}
		
		return result;
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Map<ShipSystem, Integer>> getSystemCoolantAllocated() {
		return systemCoolantAllocated;
	}
	private final DerivedProperty<Map<ShipSystem, Integer>> systemCoolantAllocated = new DerivedProperty<>( () -> {
		
		Map<ShipSystem, Integer> result = new HashMap<>();
		for(ShipSystem system: ShipSystem.values()) {
			if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
				result.put(system, 0);
			}
			else {
				result.put(system, this.worldAwareServer.getSystemManager().getPlayerShip(0).getSystemCoolant(system));							
			}
		}
		
		return result;
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Map<ShipSystem, Integer>> getSystemHeat() {
		return systemHeat;
	}
	private final DerivedProperty<Map<ShipSystem, Integer>> systemHeat = new DerivedProperty<>( () -> {
		
		Map<ShipSystem, Integer> result = new HashMap<>();
		for(ShipSystem system: ShipSystem.values()) {
			if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
				result.put(system, 0);
			}
			else {
				result.put(system, (int) (this.worldAwareServer.getSystemManager().getPlayerShip(0).getSystemHeat(system) * 100));							
			}

		}
		
		return result;
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Map<ShipSystem, Integer>> getSystemHealth() {
		return systemHealth;
	}
	private final DerivedProperty<Map<ShipSystem, Integer>> systemHealth = new DerivedProperty<>( () -> {
		
		Map<ShipSystem, Integer> result = new HashMap<>();
		for(ShipSystem system: ShipSystem.values()) {
			if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
				result.put(system, 100);
			}
			else {
				result.put(system, (int) (this.worldAwareServer.getSystemManager().getHealthOfSystem(system) * 100));							
			}
		}
		
		return result;
	}, systemManagerChangeObservable);

	@Override
	public Property<Integer> getTotalShipCoolant() {
		return totalShipCoolant;
	}
	private final DerivedProperty<Integer> totalShipCoolant = new DerivedProperty<>( () -> {
		if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return 8;
		}
		return this.worldAwareServer.getSystemManager().getPlayerShip(0).getAvailableCoolant();
	}, systemManagerChangeObservable);
	
	
	@Override
	public Property<Integer> getTotalCoolantRemaining() {
		return totalCoolantRemaining;
	}
	private final DerivedProperty<Integer> totalCoolantRemaining = new DerivedProperty<>( () -> {
		
		if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return Artemis.DEFAULT_COOLANT;
		}
		Map<ShipSystem, Integer> systemCoolantAllocated = this.getSystemCoolantAllocated().get();
		final int totalCoolantUsed = Arrays.stream(ShipSystem.values()).mapToInt(system -> systemCoolantAllocated.get(system)).sum();
		return getTotalShipCoolant().get() - totalCoolantUsed;
	}, systemManagerChangeObservable, systemCoolantAllocated, totalShipCoolant);

			
		
	@Override
	public Property<Map<GridCoord, Float>> getGridHealth() {
		return gridHealth;
	}
	private final DerivedProperty<Map<GridCoord, Float>> gridHealth = new DerivedProperty<>( () -> {
		Map<GridCoord, Float> result = new HashMap<>();
		if (this.worldAwareServer != null) {
			for (Entry<GridCoord, Float> entry : this.worldAwareServer.getSystemManager().getGridDamages()) {
				result.put(entry.getKey(), 1.0f - entry.getValue());
			}			
		}
		return result;
	}, systemManagerChangeObservable);
	
	
	@Override
	protected Property<List<DamconStatus>> getRawDamconStatus() {
		return damconStatus;
	}
	private final DerivedProperty<List<DamconStatus>> damconStatus = new DerivedProperty<>( () -> {
		List<DamconStatus> teams = new ArrayList<>();
		if (this.worldAwareServer != null) {
			for(int teamNumber = 0; teamNumber < 16; teamNumber++) {
				DamconStatus damcon = this.worldAwareServer.getSystemManager().getDamcon(teamNumber);
				if (damcon != null) {
					teams.add(cloneDamconStatus(damcon)); //DRAGONS: framework internally reuses damcon instances which messes up equality checking
				}
			}
		}
		return teams;
	}, systemManagerChangeObservable);
	
	private static DamconStatus cloneDamconStatus(DamconStatus damcon) {
		return new DamconStatus(damcon.getTeamNumber(), damcon.getMembers(), 
				damcon.getGoal().getX(), damcon.getGoal().getY(), damcon.getGoal().getZ(), 
				damcon.getPosition().getX(), damcon.getPosition().getY(), damcon.getPosition().getZ(), 
				damcon.getProgress());
	}
	
	
	@Override
	public Property<Float> getTotalEnergyRemaining() {
		return totalEnergyRemaining;
	}
	private final DerivedProperty<Float> totalEnergyRemaining = new DerivedProperty<>( () -> {
		if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return 0f;
		}
		
		return this.worldAwareServer.getSystemManager().getPlayerShip(0).getEnergy();
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Integer> getFrontShieldStrength() {
		return frontShieldStrength;
	}
	private final DerivedProperty<Integer> frontShieldStrength = new DerivedProperty<>( () -> {
		if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return 0;
		}
		
		return (int) this.worldAwareServer.getSystemManager().getPlayerShip(0).getShieldsFront();
	}, systemManagerChangeObservable);
	
	
	@Override
	public Property<Integer> getRearShieldStrength() {
		return rearShieldStrength;
		
	}
	private final DerivedProperty<Integer> rearShieldStrength = new DerivedProperty<>( () -> {
		if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return 0;
		}
		
		return (int) this.worldAwareServer.getSystemManager().getPlayerShip(0).getShieldsFront();
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Integer> getFrontShieldMaxStrength() {
		return frontShieldMaxStrength;
	}
	private final DerivedProperty<Integer> frontShieldMaxStrength = new DerivedProperty<>( () -> {
		if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return 0;
		}
		
		return (int) this.worldAwareServer.getSystemManager().getPlayerShip(0).getShieldsFront();
	}, systemManagerChangeObservable);
	
	
	@Override
	public Property<Integer> getRearShieldMaxStrength() {
		return rearShieldMaxStrength;
		
	}
	private final DerivedProperty<Integer> rearShieldMaxStrength = new DerivedProperty<>( () -> {
		if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return 0;
		}
		
		return (int) this.worldAwareServer.getSystemManager().getPlayerShip(0).getShieldsFront();
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Boolean> getShieldsActive() {
		return shieldsActive;		
	}
	
	private final DerivedProperty<Boolean> shieldsActive = new DerivedProperty<>( () -> {
		if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return false;
		}
		
		return this.worldAwareServer.getSystemManager().getPlayerShip(0).getShieldsState().getBooleanValue();
	}, systemManagerChangeObservable);

	
	
	@Override
	public void incrementSystemEnergyAllocated(ShipSystem system, int amount) {
		if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return;
		}
		super.incrementSystemEnergyAllocated(system, amount);
	}
	
	@Override
	public void incrementSystemCoolantAllocated(ShipSystem system, int amount) {
		if (this.worldAwareServer == null || this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return;
		}
		super.incrementSystemCoolantAllocated(system, amount);
	}
	
	@Override
	protected void updateSystemEnergyAllocated(ShipSystem system, int amount) {
		this.worldAwareServer.getServer().send(new EngSetEnergyPacket(system, amount));		
	}
	
	@Override
	protected void updateSystemCoolantAllocated(ShipSystem system, int amount) {
		this.worldAwareServer.getServer().send(new EngSetCoolantPacket(system, amount));		
	}
	
	public void moveDamconTeam(int teamId, GridCoord coord) {
		System.out.println("Moving DAMCON team " + teamId + " to grid " + coord);
		this.worldAwareServer.getServer().send(new EngSendDamconPacket(teamId, coord));
	}
}
