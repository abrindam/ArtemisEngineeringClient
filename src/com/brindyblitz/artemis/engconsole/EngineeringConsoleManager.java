package com.brindyblitz.artemis.engconsole;

import java.util.List;
import java.util.Map;

import com.brindyblitz.artemis.utils.newton.Property;

import com.walkertribe.ian.enums.OrdnanceType;
import com.walkertribe.ian.enums.ShipSystem;
import com.walkertribe.ian.protocol.core.eng.EngGridUpdatePacket.DamconStatus;
import com.walkertribe.ian.util.GridCoord;
import com.walkertribe.ian.vesseldata.VesselNode;
import com.walkertribe.ian.vesseldata.VesselNodeConnection;

public interface EngineeringConsoleManager {
	
	void connect(String host);
	
	void connect(String host, int port);
	
	void disconnect();
	
	Property<GameState> getGameState();
	
	Property<Integer> getTotalShipCoolant();

	Property<Map<ShipSystem, Integer>> getSystemEnergyAllocated();

	Property<Map<ShipSystem, Integer>> getSystemCoolantAllocated();
	
	Property<Map<ShipSystem, Integer>> getSystemHeat();
	
	Property<Map<ShipSystem, Integer>> getSystemHealth();

	Property<Integer> getTotalCoolantRemaining();
	
	Property<Float> getTotalEnergyRemaining(); 
	
	Property<Map<GridCoord, Float>> getGridHealth();

	List<VesselNode> getGrid();
	
	List<VesselNodeConnection> getGridConnections();
	
	Property<List<EnhancedDamconStatus>> getDamconTeams();
	
	Property<Integer> getFrontShieldStrength();

	Property<Integer> getRearShieldStrength();
	
	Property<Integer> getFrontShieldMaxStrength();

	Property<Integer> getRearShieldMaxStrength();
	
	Property<Boolean> getShieldsActive();
	
	Property<Map<OrdnanceType, Integer>> getOrdnanceCount();
	
	Property<Boolean> getAutoDamcon();

	void setSystemEnergyAllocated(ShipSystem system, int amount);

	void setSystemCoolantAllocated(ShipSystem system, int amount);
	
	void incrementSystemEnergyAllocated(ShipSystem system, int amount);

	void incrementSystemCoolantAllocated(ShipSystem system, int amount);
	
	void moveDamconTeam(int teamId, GridCoord coord);
	
	void setAutoDamcon(boolean autoDamcon);

	void resetEnergy();

	void resetCoolant();
	
	
	public enum GameState {
		DISCONNECTED, PREGAME, INGAME, GAMEOVER
	}
	
	public enum Events {
		CHANGE, GAME_STATE_CHANGE
	}
	
	public class EnhancedDamconStatus {
		private DamconStatus damconStatus;
		private float x;
		private float y;
		private float z;
		
		public EnhancedDamconStatus(DamconStatus damconStatus, float x, float y, float z) {
			this.damconStatus = damconStatus;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
        public int getTeamNumber() {
            return damconStatus.getTeamNumber();
        }
     
        public int getMembers() {
            return damconStatus.getMembers();
        }
        
        public float getX() {
			return this.x;
		}
        
        public float getY() {
			return this.y;
		}
        
        public float getZ() {
			return this.z;
		}
        
		@Override
		public String toString() {
			return "Team #" + getTeamNumber() + " (" + getMembers() + " members) @ " +
					"(" + this.x + "," + this.y + "," + this.z + ")";
		}
	}
}