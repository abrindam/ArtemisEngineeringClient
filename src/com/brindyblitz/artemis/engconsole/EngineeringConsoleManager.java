package com.brindyblitz.artemis.engconsole;

import java.util.List;
import java.util.Map;

import com.brindyblitz.artemis.utils.newton.Property;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.protocol.core.eng.EngGridUpdatePacket.DamconStatus;
import net.dhleong.acl.util.GridCoord;
import net.dhleong.acl.vesseldata.VesselNode;
import net.dhleong.acl.vesseldata.VesselNodeConnection;

public interface EngineeringConsoleManager {
	
	void connect(String host);
	
	void connect(String host, int port);
	
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

	void setSystemEnergyAllocated(ShipSystem system, int amount);

	void setSystemCoolantAllocated(ShipSystem system, int amount);
	
	void incrementSystemEnergyAllocated(ShipSystem system, int amount);

	void incrementSystemCoolantAllocated(ShipSystem system, int amount);
	
	void moveDamconTeam(int teamId, GridCoord coord);

	void resetEnergy();

	void resetCoolant();
	
	public enum GameState {
		DISCONNECTED, PREGAME, INGAME
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