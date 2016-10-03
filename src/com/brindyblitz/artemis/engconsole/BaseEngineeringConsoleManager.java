package com.brindyblitz.artemis.engconsole;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.brindyblitz.artemis.protocol.NonShittyShipSystemGrid;
import com.brindyblitz.artemis.utils.newton.DerivedProperty;
import com.brindyblitz.artemis.utils.newton.Property;
import com.brindyblitz.artemis.utils.newton.SettableProperty;
import com.walkertribe.ian.Context;
import com.walkertribe.ian.enums.ShipSystem;
import com.walkertribe.ian.protocol.core.eng.EngGridUpdatePacket.DamconStatus;
import com.walkertribe.ian.util.GridCoord;
import com.walkertribe.ian.util.ShipSystemGrid;
import com.walkertribe.ian.vesseldata.FilePathResolver;
import com.walkertribe.ian.vesseldata.Vessel;
import com.walkertribe.ian.vesseldata.VesselData;
import com.walkertribe.ian.vesseldata.VesselNode;
import com.walkertribe.ian.vesseldata.VesselNodeConnection;
import com.walkertribe.ian.world.Artemis;

public abstract class BaseEngineeringConsoleManager implements EngineeringConsoleManager {

	private ShipSystemGrid shipSystemGrid;
	private List<VesselNode> grid;
	private Map<GridCoord, VesselNode> gridIndex;
	private List<VesselNodeConnection> gridConnections;
	protected final Context context;
	protected int shipNumber = 1;
	private boolean needsResetForNewGame = false;
	
	
	public BaseEngineeringConsoleManager() {
		
		context = new Context(new FilePathResolver(new File(System.getProperty("user.dir"), "artemisData")));
		
		NonShittyShipSystemGrid shipSystemGrid = new NonShittyShipSystemGrid();
		this.grid = new ArrayList<>();
		this.gridIndex = new HashMap<>();
		Vessel vessel = VesselData.load(context).getVessel(0);
		Iterator<VesselNode> nodeIterator = vessel.getInternals().nodeIterator();
		while (nodeIterator.hasNext()) {
			VesselNode node = nodeIterator.next();
			grid.add(node);
			gridIndex.put(node.getGridCoord(), node);
			if (node.getSystem() != null) {
				shipSystemGrid.addNode(node.getSystem(), node.getGridCoord());				
			}
		}
		
		this.gridConnections = new ArrayList<>();
		Iterator<VesselNodeConnection> connectionIterator = vessel.getInternals().connectionIterator();
		while(connectionIterator.hasNext()) {
			gridConnections.add(connectionIterator.next());
		}
		
		this.shipSystemGrid = shipSystemGrid;
	}
	
	protected void afterChildConstructor() {
		this.getGameState().onChange(() -> {
			if (this.getGameState().get() == GameState.PREGAME) {
				if (needsResetForNewGame) {
					needsResetForNewGame = false;
					resetForNewGame();
				}
			}
			else if (this.getGameState().get() == GameState.INGAME) {
				needsResetForNewGame = true;
			}
		});
	}
	
	public void resetForNewGame() {
		System.out.println("Resetting for new game");
		playerReady.set(false);
	}
	
	@Override
	public Property<Boolean> getPlayerReady() {
		return playerReady;
	}
	private final SettableProperty<Boolean> playerReady = new SettableProperty<>(false);

	
	@Override
	public void setSystemEnergyAllocated(ShipSystem system, int amount) {
		updateSystemEnergyAllocated(system, Math.min(Artemis.MAX_ENERGY_ALLOCATION_PERCENT, Math.max(0, amount)));
	}
	
	@Override
	public void incrementSystemEnergyAllocated(ShipSystem system, int amount) {
		updateSystemEnergyAllocated(system, Math.min(Artemis.MAX_ENERGY_ALLOCATION_PERCENT, Math.max(0,this.getSystemEnergyAllocated().get().get(system) + amount)));
	}
	
	@Override
	public List<VesselNode> getGrid() {
		return this.grid;
	}
	
	@Override
	public List<VesselNodeConnection> getGridConnections() {
		return this.gridConnections;
	}
	
	@Override
	public Property<List<EnhancedDamconStatus>> getDamconTeams() {
		// HACK - this depends on getRawDamconStatus(), but that doesn't exist at construction time
		// because the subclass initialized AFTER the superclass. So cheat with a lazy init.
		if (damconTeams == null) {
			 damconTeams = new DerivedProperty<>( () -> {
					List<EnhancedDamconStatus> result = new ArrayList<>();
					for (DamconStatus damconStatus : this.getRawDamconStatus().get()) {
						VesselNode positionNode = gridIndex.get(damconStatus.getPosition());
						VesselNode goalNode = gridIndex.get(damconStatus.getGoal());
						// Turns out progress = 0 means at GOAL node, while 1 = at position node.
						// So its "percentage distance remaining" I guess. Would have expected opposite.
						float x = (positionNode.getX() - goalNode.getX()) * damconStatus.getProgress() + goalNode.getX();
						float y = (positionNode.getY() - goalNode.getY()) * damconStatus.getProgress() + goalNode.getY();
						float z = (positionNode.getZ() - goalNode.getZ()) * damconStatus.getProgress() + goalNode.getZ();
						
						result.add(new EnhancedDamconStatus(damconStatus, x, y, z));
					}
					return result;
				}, getRawDamconStatus());
		}
		
		return damconTeams;
	}	
	private DerivedProperty<List<EnhancedDamconStatus>> damconTeams;
	
	protected abstract Property<List<DamconStatus>> getRawDamconStatus();
	
	protected ShipSystemGrid getShipSystemGrid() {
		return shipSystemGrid;
	}
	
	@Override
	public void selectShip(int shipNumber) {
		this.shipNumber = shipNumber;		
	}
	
	@Override
	public void ready() {
		this.playerReady.set(true);
	}
	
	protected abstract void updateSystemEnergyAllocated(ShipSystem system, int amount);
	
	@Override
	public void setSystemCoolantAllocated(ShipSystem system, int amount) {
		updateSystemCoolantAllocated(system, Math.max(0, Math.min(amount, getTotalShipCoolant().get())));
		
	}
	
	@Override
	public void incrementSystemCoolantAllocated(ShipSystem system, int amount) {
		updateSystemCoolantAllocated(system, Math.max(0, this.getSystemCoolantAllocated().get().get(system) + Math.min(amount, getTotalCoolantRemaining().get())));
	}
	
	protected abstract void updateSystemCoolantAllocated(ShipSystem system, int amount);
	
	@Override
	public void resetEnergy() {
		for (ShipSystem system: ShipSystem.values()) {
			updateSystemEnergyAllocated(system, 100);
		}
	}
	
	@Override
	public void resetCoolant() {
		for (ShipSystem system: ShipSystem.values()) {
			updateSystemCoolantAllocated(system, 0);
		}
	}

}
