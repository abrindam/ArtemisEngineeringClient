package com.brindyblitz.artemis.engconsole;

import java.util.List;
import java.util.Map;

import com.brindyblitz.artemis.engconsole.ui.damcon.Damcon;

import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.protocol.core.eng.EngGridUpdatePacket.DamconStatus;
import net.dhleong.acl.util.GridCoord;
import net.dhleong.acl.vesseldata.VesselNode;
import net.dhleong.acl.vesseldata.VesselNodeConnection;

public interface EngineeringConsoleManager {
	int getTotalShipCoolant();

	int getSystemEnergyAllocated(ShipSystem system);

	int getSystemCoolantAllocated(ShipSystem system);
	
	int getSystemHeat(ShipSystem system);
	
	int getSystemHealth(ShipSystem system);

	int getTotalCoolantRemaining();
	
	Map<GridCoord, Float> getGridHealth();

	List<VesselNode> getGrid();
	
	List<VesselNodeConnection> getGridConnections();
	
	List<EnhancedDamconStatus> getDamconTeams();

	void setSystemEnergyAllocated(ShipSystem system, int amount);

	void setSystemCoolantAllocated(ShipSystem system, int amount);
	
	void incrementSystemEnergyAllocated(ShipSystem system, int amount);

	void incrementSystemCoolantAllocated(ShipSystem system, int amount);

	void addChangeListener(EngineeringConsoleChangeListener listener);
	
	public static interface EngineeringConsoleChangeListener {
		public void onChange();
	}

	void resetEnergy();

	void resetCoolant();
	
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
			return x;
		}
        
        public float getY() {
			return y;
		}
        
        public float getZ() {
			return z;
		}
        
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
        	b.append("Team #").append(getTeamNumber())
        	.append(" (").append(getMembers()).append("): ")
        	.append(" (").append(x).append(", ").append(y).append(", ").append(z).append(")");
        	return b.toString();
		}
	}
}