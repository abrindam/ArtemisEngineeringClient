package com.brindyblitz.artemis.engconsole;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.brindyblitz.artemis.protocol.NonShittyShipSystemGrid;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.util.ShipSystemGrid;
import net.dhleong.acl.vesseldata.Vessel;
import net.dhleong.acl.vesseldata.VesselData;
import net.dhleong.acl.vesseldata.VesselNode;
import net.dhleong.acl.world.Artemis;

public abstract class BaseEngineeringConsoleManager implements EngineeringConsoleManager {

	private List<EngineeringConsoleChangeListener> listeners = new ArrayList<>();
	private ShipSystemGrid shipSystemGrid;
	private List<VesselNode> grid;
	
	
	public BaseEngineeringConsoleManager() {
	
		NonShittyShipSystemGrid shipSystemGrid = new NonShittyShipSystemGrid();
		this.grid = new ArrayList<>();
		Vessel vessel = VesselData.get().getVessel(0);
		Iterator<VesselNode> nodeIterator = vessel.getInternals().nodeIterator();
		while (nodeIterator.hasNext()) {
			VesselNode node = nodeIterator.next();
			grid.add(node);
			if (node.getSystem() != null) {
				shipSystemGrid.addNode(node.getSystem(), node.getGridCoord());				
			}
		}
		
		this.shipSystemGrid = shipSystemGrid;
	}
	
	protected void fireChange() {
		for (EngineeringConsoleChangeListener listener: listeners) {
			listener.onChange();
		}
	}
	
	public void addChangeListener(EngineeringConsoleChangeListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public void setSystemEnergyAllocated(ShipSystem system, int amount) {
		updateSystemEnergyAllocated(system, Math.min(Artemis.MAX_ENERGY_ALLOCATION_PERCENT, Math.max(0, amount)));
	}
	
	@Override
	public void incrementSystemEnergyAllocated(ShipSystem system, int amount) {
		updateSystemEnergyAllocated(system, Math.min(Artemis.MAX_ENERGY_ALLOCATION_PERCENT, Math.max(0,this.getSystemEnergyAllocated(system) + amount)));
	}
	
	@Override
	public List<VesselNode> getGrid() {
		return this.grid;
	}
	
	protected ShipSystemGrid getShipSystemGrid() {
		return shipSystemGrid;
	}
	
	protected abstract void updateSystemEnergyAllocated(ShipSystem system, int amount);
	
	@Override
	public void setSystemCoolantAllocated(ShipSystem system, int amount) {
		updateSystemCoolantAllocated(system, Math.max(0, Math.min(amount, getTotalShipCoolant())));
		
	}
	
	@Override
	public void incrementSystemCoolantAllocated(ShipSystem system, int amount) {
		updateSystemCoolantAllocated(system, Math.max(0, this.getSystemCoolantAllocated(system) + Math.min(amount, getTotalCoolantRemaining())));
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
