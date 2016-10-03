package com.brindyblitz.artemis.engconsole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.brindyblitz.artemis.protocol.EnhancedSystemManager;
import com.brindyblitz.artemis.protocol.WorldAwareRegularServer;
import com.brindyblitz.artemis.protocol.WorldAwareRobustProxyListener;
import com.brindyblitz.artemis.protocol.WorldAwareServer;
import com.brindyblitz.artemis.utils.AudioManager;
import com.brindyblitz.artemis.utils.newton.DerivedProperty;
import com.brindyblitz.artemis.utils.newton.ObservableAdapter;
import com.brindyblitz.artemis.utils.newton.Property;
import com.walkertribe.ian.enums.Console;
import com.walkertribe.ian.enums.OrdnanceType;
import com.walkertribe.ian.enums.ShipSystem;
import com.walkertribe.ian.enums.TargetingMode;
import com.walkertribe.ian.protocol.core.eng.EngGridUpdatePacket.DamconStatus;
import com.walkertribe.ian.protocol.core.eng.EngSendDamconPacket;
import com.walkertribe.ian.protocol.core.eng.EngSetAutoDamconPacket;
import com.walkertribe.ian.protocol.core.eng.EngSetCoolantPacket;
import com.walkertribe.ian.protocol.core.eng.EngSetEnergyPacket;
import com.walkertribe.ian.protocol.core.setup.SetConsolePacket;
import com.walkertribe.ian.protocol.core.setup.SetShipPacket;
import com.walkertribe.ian.util.GridCoord;
import com.walkertribe.ian.world.Artemis;
import com.walkertribe.ian.world.ArtemisPlayer;

public class RealEngineeringConsoleManager extends BaseEngineeringConsoleManager {

	private WorldAwareServer worldAwareServer;
	private boolean proxy;
	private ObservableAdapter connectionStateChangeObservable = new ObservableAdapter();
	private ObservableAdapter systemManagerChangeObservable = new ObservableAdapter();
	
	private AudioManager audioManager;

	public RealEngineeringConsoleManager(boolean proxy) {
		this.proxy = proxy;	
		afterChildConstructor();
	}
	
	public void connect(String host) {
		this.connect(host, 2010);
	}
	
	public void connect(String host, int port) {
		if (proxy) {
			this.worldAwareServer = new WorldAwareRobustProxyListener(host, port, port, context);
		}
		else {
			try {
				this.worldAwareServer = new WorldAwareRegularServer(host, port, context);
			} catch (IOException e) {
				//abort without updating state
				return;
			}			
		}
		boolean connectProcessed = false;
		this.worldAwareServer.onEvent(WorldAwareServer.Events.CONNECTION_STATE_CHANGE, () -> {
			connectionStateChangeObservable.triggerChange();
			if (!connectProcessed) {
				sendShipAndConsoleChoice();

				// TODO: ! hack so you can get into the game - remove once UI for marking ready is done
				// System.out.println("Temporarily marking ready");
				// ready();
			}
		});
				
		this.worldAwareServer.getSystemManager().events.on(EnhancedSystemManager.Events.CHANGE, () -> systemManagerChangeObservable.triggerChange());
		this.worldAwareServer.getSystemManager().setPermanantSystemGrid(getShipSystemGrid());
	}
	
	@Override
	public void selectShip(int shipNumber) {
		super.selectShip(shipNumber);
		// TODO: !! sleep here?
		sendShipAndConsoleChoice();
	}
	
	@Override
	public void ready() {
		super.ready();
		
		if (worldAwareServer != null && this.worldAwareServer.isConnected()) {
			// TODO: ! DBG
			System.out.println("Sending ready");
			
			this.worldAwareServer.ready();
		}
	}
	
	private void sendShipAndConsoleChoice() {
		if (worldAwareServer != null && this.worldAwareServer.isConnected()) {
			this.worldAwareServer.getServer().send(new SetShipPacket(shipNumber));
			this.worldAwareServer.getServer().send(new SetConsolePacket(Console.ENGINEERING, true));
			
			System.out.println("Selecting engineering console on ship number " + this.shipNumber);
		}
	}
	
	public void disconnect() {
		this.worldAwareServer.disconnect();
	}
	
	@Override
	public Property<GameState> getGameState() {
		return gameState;
	}
	private final DerivedProperty<GameState> gameState = new DerivedProperty<>(() -> {	
		if (worldAwareServer == null || !worldAwareServer.isConnected()) {
			return GameState.DISCONNECTED;
		}
		
		if (this.worldAwareServer.getSystemManager().isGameOverScreen()) {
			return GameState.GAMEOVER;
		}
		else if (this.getCurrentShip() != null && getPlayerReady().get()) {
			return GameState.INGAME;
		}
		else {
			return GameState.PREGAME;
		}
	}, systemManagerChangeObservable, connectionStateChangeObservable, getPlayerReady());
	
	
	@Override
	public Property<Map<ShipSystem, Integer>> getSystemEnergyAllocated() {
		return systemEnergyAllocated;
	}
	private final DerivedProperty<Map<ShipSystem, Integer>> systemEnergyAllocated = new DerivedProperty<>(() -> {
		
		Map<ShipSystem, Integer> result = new HashMap<>();
		for(ShipSystem system: ShipSystem.values()) {
			if (this.gameState.get() != GameState.INGAME) {
				result.put(system, 100);
			}
			else {
				result.put(system, (int)(this.getCurrentShip().getSystemEnergy(system) * 300));							
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
			if (this.gameState.get() != GameState.INGAME) {
				result.put(system, 0);
			}
			else {
				result.put(system, this.getCurrentShip().getSystemCoolant(system));							
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
			if (this.gameState.get() != GameState.INGAME) {
				result.put(system, 0);
			}
			else {
				result.put(system, (int) (this.getCurrentShip().getSystemHeat(system) * 100));							
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
			if (this.gameState.get() != GameState.INGAME) {
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
		if (this.gameState.get() != GameState.INGAME) {
			return 8;
		}
		return this.getCurrentShip().getAvailableCoolant();
	}, systemManagerChangeObservable);
	
	
	@Override
	public Property<Integer> getTotalCoolantRemaining() {
		return totalCoolantRemaining;
	}
	private final DerivedProperty<Integer> totalCoolantRemaining = new DerivedProperty<>( () -> {
		
		if (this.gameState.get() != GameState.INGAME) {
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
		if (this.gameState.get() != GameState.INGAME) {
			return 0f;
		}
		
		return this.getCurrentShip().getEnergy();
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Integer> getFrontShieldStrength() {
		return frontShieldStrength;
	}
	private final DerivedProperty<Integer> frontShieldStrength = new DerivedProperty<>( () -> {
		if (this.gameState.get() != GameState.INGAME) {
			return 0;
		}
		
		// When shields are knocked offline, they go to a negative value and come back online once they reach 1 again.
		int strength = (int) this.getCurrentShip().getShieldsFront();
		return strength > 0 ? strength : 0;
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Integer> getRearShieldStrength() {
		return rearShieldStrength;
		
	}
	private final DerivedProperty<Integer> rearShieldStrength = new DerivedProperty<>( () -> {
		if (this.gameState.get() != GameState.INGAME) {
			return 0;
		}
		
		// When shields are knocked offline, they go to a negative value and come back online once they reach 1 again.
		int strength = (int) this.getCurrentShip().getShieldsRear();
		return strength > 0 ? strength : 0;
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Integer> getFrontShieldMaxStrength() {
		return frontShieldMaxStrength;
	}
	private final DerivedProperty<Integer> frontShieldMaxStrength = new DerivedProperty<>( () -> {
		if (this.gameState.get() != GameState.INGAME) {
			return 0;
		}
		
		return (int) this.getCurrentShip().getShieldsFrontMax();
	}, systemManagerChangeObservable);
	
	
	@Override
	public Property<Integer> getRearShieldMaxStrength() {
		return rearShieldMaxStrength;
		
	}
	private final DerivedProperty<Integer> rearShieldMaxStrength = new DerivedProperty<>( () -> {
		if (this.gameState.get() != GameState.INGAME) {
			return 0;
		}
		
		return (int) this.getCurrentShip().getShieldsRearMax();
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Boolean> getShieldsActive() {
		return shieldsActive;
	}
	
	private final DerivedProperty<Boolean> shieldsActive = new DerivedProperty<>( () -> {
		if (this.gameState.get() != GameState.INGAME) {
			return false;
		}
		
		return this.getCurrentShip().getShieldsState().getBooleanValue();
	}, systemManagerChangeObservable);

	@Override
	public Property<Map<OrdnanceType, Integer>> getOrdnanceCount() {
		return ordnanceCount;
	}
	private final DerivedProperty<Map<OrdnanceType, Integer>> ordnanceCount = new DerivedProperty<>( () -> {
		
		Map<OrdnanceType, Integer> result = new HashMap<>();
		for(OrdnanceType type: OrdnanceType.values()) {
			if (this.gameState.get() != GameState.INGAME) {
				result.put(type, 0);
			}
			else { 
				result.put(type, this.getCurrentShip().getTorpedoCount(type));							
			}
		}
		
		return result;
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Boolean> getAutoDamcon() {
		return autoDamcon;
	}
	private final DerivedProperty<Boolean> autoDamcon = new DerivedProperty<>( () -> {
		if (this.gameState.get() != GameState.INGAME) {
			return true;
		}
		
		return this.worldAwareServer.getSystemManager().getAutoDamcon();
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Boolean> getWeaponsLocked() {
		return weaponsLocked;
	}
	private final DerivedProperty<Boolean> weaponsLocked = new DerivedProperty<>( () -> {
		if (this.gameState.get() != GameState.INGAME) {
			return false;
		}
		return this.getCurrentShip().getWeaponsTarget() != 0;
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Boolean> getAutoBeams() {
		return autoBeams;
	}
	private final DerivedProperty<Boolean> autoBeams = new DerivedProperty<>( () -> {
		if (this.gameState.get() != GameState.INGAME) {
			return true;
		}
		//assume AUTO if null
		//DRAGONS: current bug in IAN - AUTO and MANUAL are switched
		return this.getCurrentShip().getTargetingMode() != TargetingMode.AUTO;
	}, systemManagerChangeObservable);
	
	@Override
	public Property<Ship[]> getAllShips() {
		return allShips;
	}
	
	private final DerivedProperty<Ship[]> allShips = new DerivedProperty<>( () -> {
		if (this.gameState.get() == GameState.DISCONNECTED) {
			return new Ship[Artemis.SHIP_COUNT];
		}
		
		Ship[] ships = new Ship[Artemis.SHIP_COUNT];
		int i = 0;
		for (com.walkertribe.ian.protocol.core.setup.AllShipSettingsPacket.Ship ship: this.worldAwareServer.getSystemManager().getShips()) {
			ships[i] = new Ship(ship.getName(), ShipType.byId(ship.getShipType()), i + 1);
			i++;
		}
		return ships;
	}, systemManagerChangeObservable);
	
	private ArtemisPlayer getCurrentShip() {
		// System.out.println("Getting current ship with number " + shipNumber);
		// System.out.println("ship null? " + (this.worldAwareServer.getSystemManager().getPlayerShip(1) == null));
		// TODO: ! dbg
		return this.worldAwareServer.getSystemManager().getPlayerShip(1); // shipNumber);
	}
	
	
	@Override
	public void incrementSystemEnergyAllocated(ShipSystem system, int amount) {
		if (this.gameState.get() != GameState.INGAME) {
			return;
		}
		super.incrementSystemEnergyAllocated(system, amount);
	}
	
	@Override
	public void incrementSystemCoolantAllocated(ShipSystem system, int amount) {
		if (this.gameState.get() != GameState.INGAME) {
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
	
	public void setAutoDamcon(boolean autoDamcon) {
		this.worldAwareServer.getServer().send(new EngSetAutoDamconPacket(autoDamcon));
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
