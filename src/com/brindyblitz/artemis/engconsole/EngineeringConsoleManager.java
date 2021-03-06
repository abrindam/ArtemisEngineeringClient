package com.brindyblitz.artemis.engconsole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brindyblitz.artemis.utils.AudioManager;
import com.brindyblitz.artemis.utils.newton.Property;
import com.walkertribe.ian.enums.OrdnanceType;
import com.walkertribe.ian.enums.ShipSystem;
import com.walkertribe.ian.protocol.core.eng.EngGridUpdatePacket.DamconStatus;
import com.walkertribe.ian.util.GridCoord;
import com.walkertribe.ian.vesseldata.VesselNode;
import com.walkertribe.ian.vesseldata.VesselNodeConnection;

public interface EngineeringConsoleManager {
	//////////////////
	// Connectivity //
	//////////////////
	void connect(String host);
	void connect(String host, int port);
	void disconnect();
	
	/////////
	// Get //
	/////////
	Property<Boolean> getPlayerReady();
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
	Property<Boolean> getWeaponsLocked();
	Property<Boolean> getAutoBeams();
	void setSystemEnergyAllocated(ShipSystem system, int amount);
	void setSystemCoolantAllocated(ShipSystem system, int amount);
	Property<Ship[]> getAllShips();
	
	/////////
	// Set //
	/////////
	void selectShip(int shipNumber);
	void ready();
	void incrementSystemEnergyAllocated(ShipSystem system, int amount);
	void incrementSystemCoolantAllocated(ShipSystem system, int amount);
	void moveDamconTeam(int teamId, GridCoord coord);
	void setAutoDamcon(boolean autoDamcon);
	void resetEnergy();
	void resetCoolant();
	
	//////////
	// Misc //
	//////////
	public void setAudioManager(AudioManager audioManager);
	public AudioManager getAudioManager();
	
	///////////
	// Enums //
	///////////
	public enum GameState {
		DISCONNECTED, PREGAME, INGAME, GAMEOVER
	}
	
	public enum Events {
		CHANGE, GAME_STATE_CHANGE
	}
	
	//////////////////////////
	// EnhancedDamconStatus //
	//////////////////////////
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
	
	public class Ship {
		private String name;
		private ShipType type;
		private int shipNumber;

		public Ship(String name, ShipType type, int shipNumber) {
			this.name = name;
			this.type = type;
			this.shipNumber = shipNumber;
		}
		
		@Override
		public String toString() {
			return "Ship #" + shipNumber + ": " + name + " (" + type.getDescription() + ")";
		}

		public String getName() {
			return this.name;
		}
		
		public ShipType getType() {
			return this.type;
		}
		
		public int getShipNumber()
		{
			return this.shipNumber;
		}
	}
	
	public enum ShipType {		
		TSN_Light_Cruiser(0, "Light Cruiser"),
		TSN_Scout(1, "Scout"),
		TSN_Battleship(2, "Battleship"),
		TSN_Missile_Cruiser(3, "Missile Cruiser"),
		TSN_Dreadnought(4, "Dreadnought"),
		TSN_Carrier(5, "Carrier"),
		TSN_Mine_Layer(6, "Mine Layer"),
		TSN_Juggernaut(7, "Juggernaut"),
		Ximni_Light_Cruiser(8, "Ximni Light Cruiser"),
		Ximni_Scout(9, "Ximni Scout"),
		Ximni_MissileCruiser(10, "Ximni Missile Cruiser"),
		Ximni_Battleship(11, "Ximni Battleship"),
		Ximni_Carrier(12, "Ximni Carrier"),
		Ximni_Dreadnought(13, "Ximni Dreadnought");
		
		private static Map<Integer, ShipType> idMap = new HashMap<>();
		private int id;
		private String description;

		private ShipType(int id, String description) {
			this.id = id;
			this.description = description;
		}
		
		static {
			for (ShipType shipType : values()) {
				idMap.put(shipType.id, shipType);
			}
		}
		
		public int getId() {
			return id;
		}
		
		public String getDescription() {
			return description;
		}
		
		public static ShipType byId(int id) {
			return idMap.get(id);
		}
	}
}