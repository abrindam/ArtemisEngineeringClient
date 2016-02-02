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

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.protocol.core.eng.EngGridUpdatePacket.DamconStatus;
import net.dhleong.acl.protocol.core.eng.EngSendDamconPacket;
import net.dhleong.acl.protocol.core.eng.EngSetCoolantPacket;
import net.dhleong.acl.protocol.core.eng.EngSetEnergyPacket;
import net.dhleong.acl.util.GridCoord;
import net.dhleong.acl.world.Artemis;

public class RealEngineeringConsoleManager extends BaseEngineeringConsoleManager {

	private WorldAwareServer worldAwareServer;
	private GameState gameState = GameState.DISCONNECTED;
	private boolean proxy;

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
		this.worldAwareServer.onEvent(WorldAwareServer.Events.CONNECTION_STATE_CHANGE, () -> this.updateGameState());
		this.worldAwareServer.getSystemManager().events.on(NotifyingSystemManager.Events.CHANGE, () -> this.systemManagerChange());
		this.worldAwareServer.getSystemManager().setSystemGrid(getShipSystemGrid());
		updateGameState();
	}
	
	private void systemManagerChange() {
		this.fireChange();
		this.updateGameState();
	}
	
	private void updateGameState() {
		GameState oldGameState = gameState;
		if (!worldAwareServer.isConnected()) {
			gameState = GameState.DISCONNECTED;
		}
		else if ( this.worldAwareServer.getSystemManager().getPlayerShip(0) != null) {
			gameState = GameState.INGAME;
		}
		else {
			gameState = GameState.PREGAME;
		}
		
		if (oldGameState != gameState) {
			eventEmitter.emit(Events.GAME_STATE_CHANGE);
		}
	}
	
	@Override
	public GameState getGameState() {
		return gameState;
	}
	
	@Override
	public int getSystemEnergyAllocated(ShipSystem system) {
		if (this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return 100;
		}
		return (int)(this.worldAwareServer.getSystemManager().getPlayerShip(0).getSystemEnergy(system) * 300);
	}
	
	@Override
	public int getSystemCoolantAllocated(ShipSystem system) {
		if (this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return 0;
		}
		return this.worldAwareServer.getSystemManager().getPlayerShip(0).getSystemCoolant(system);
	}
	
	@Override
	public int getSystemHeat(ShipSystem system) {
		if (this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return 0;
		}

		return (int) (this.worldAwareServer.getSystemManager().getPlayerShip(0).getSystemHeat(system) * 100);
	}
	
	@Override
	public int getSystemHealth(ShipSystem system) {
		if (this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return 100;
		}
		return (int) (this.worldAwareServer.getSystemManager().getHealthOfSystem(system) * 100);
	}
	
	@Override
	public int getTotalCoolantRemaining() {
		if (this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return Artemis.DEFAULT_COOLANT;
		}
		final int totalCoolantUsed = Arrays.stream(ShipSystem.values()).mapToInt(system -> this.getSystemCoolantAllocated(system)).sum();
		return getTotalShipCoolant() - totalCoolantUsed;
	}

	@Override
	public int getTotalShipCoolant() {
		if (this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return 8;
		}
		return this.worldAwareServer.getSystemManager().getPlayerShip(0).getAvailableCoolant();
	}
	
	@Override
	public Map<GridCoord, Float> getGridHealth() {
		Map<GridCoord, Float> result = new HashMap<>();
		for (Entry<GridCoord, Float> entry : this.worldAwareServer.getSystemManager().getGridDamages()) {
			result.put(entry.getKey(), 1.0f - entry.getValue());
		}
		return result;
	}
	
	@Override
	public List<DamconStatus> getRawDamconStatus() {
		List<DamconStatus> teams = new ArrayList<>();
		for(int teamNumber = 0; teamNumber < 16; teamNumber++) {
			DamconStatus damcon = this.worldAwareServer.getSystemManager().getDamcon(teamNumber);
			if (damcon != null) {
				teams.add(damcon);
			}
		}
		return teams;
	}
	
	@Override
	public float getTotalEnergyRemaining() {
		if (this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return 0;
		}
		
		return this.worldAwareServer.getSystemManager().getPlayerShip(0).getEnergy();
	}
	
	@Override
	public void incrementSystemEnergyAllocated(ShipSystem system, int amount) {
		if (this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
			return;
		}
		super.incrementSystemEnergyAllocated(system, amount);
	}
	
	@Override
	public void incrementSystemCoolantAllocated(ShipSystem system, int amount) {
		if (this.worldAwareServer.getSystemManager().getPlayerShip(0) == null) {
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
