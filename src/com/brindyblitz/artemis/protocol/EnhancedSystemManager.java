package com.brindyblitz.artemis.protocol;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.brindyblitz.artemis.utils.EventEmitter;
import com.brindyblitz.artemis.utils.EventSubscriber;
import com.walkertribe.ian.Context;
import com.walkertribe.ian.iface.Listener;
import com.walkertribe.ian.protocol.core.GameOverPacket;
import com.walkertribe.ian.protocol.core.eng.EngAutoDamconUpdatePacket;
import com.walkertribe.ian.protocol.core.eng.EngGridUpdatePacket;
import com.walkertribe.ian.protocol.core.setup.AllShipSettingsPacket;
import com.walkertribe.ian.protocol.core.world.DestroyObjectPacket;
import com.walkertribe.ian.protocol.core.world.IntelPacket;
import com.walkertribe.ian.protocol.core.world.ObjectUpdatePacket;
import com.walkertribe.ian.util.ShipSystemGrid;
import com.walkertribe.ian.world.Artemis;
import com.walkertribe.ian.world.SystemManager;

public class EnhancedSystemManager extends SystemManager {

	private EventEmitter<Events> eventEmitter = new EventEmitter<>();
	public final EventSubscriber<Events> events = eventEmitter.subscriber;
	private final ScheduledExecutorService scheduler;
	private boolean gameOverScreen;
	private ScheduledFuture<?> objectUpdateTimeout = null;
	private ShipSystemGrid permanantGrid;
	private boolean autoDamcon = true;
	private final AllShipSettingsPacket.Ship[] ships = new AllShipSettingsPacket.Ship[Artemis.SHIP_COUNT];
	
	public EnhancedSystemManager(Context context) {
		super(context);
		scheduler = Executors.newSingleThreadScheduledExecutor();		
	}
	
	@Override
	@Listener
	public void onPacket(DestroyObjectPacket pkt) {
		super.onPacket(pkt);
		this.fireChange();
	}
	
	@Override
	@Listener
	public void onPacket(EngGridUpdatePacket pkt) {
		super.onPacket(pkt);
		this.fireChange();
	}
	
	@Override
	@Listener
	public void onPacket(IntelPacket pkt) {
		super.onPacket(pkt);
		this.fireChange();
	}
	
	@Override
	@Listener
	public void onPacket(ObjectUpdatePacket pkt) {
		if (!gameOverScreen) {
			restartObjectUpdateTimeout();
		}
		super.onPacket(pkt);
		this.fireChange();
	}
	
	@Listener
	public void onAutoDamconUpdate(EngAutoDamconUpdatePacket pkt) {
		this.autoDamcon = pkt.isOn();
		this.fireChange();
	}
	
	public boolean getAutoDamcon() {
		return this.autoDamcon;
	}
	
	@Listener
	public void onCompletelyGameOver(GameOverPacket pkt) {
		this.gameOverScreen = false;
		if (objectUpdateTimeout != null) {
			objectUpdateTimeout.cancel(true);
		}
		this.clear();
		if (permanantGrid != null) {
			setSystemGrid(permanantGrid);
		}
		fireChange();
	}
	
	public boolean isGameOverScreen() {
		return gameOverScreen;
	}
	
	@Listener
	public void onShipListUpdate(AllShipSettingsPacket pkt) {
		for (int i = 1; i<= Artemis.SHIP_COUNT; i++) {
			ships[i -1] = pkt.getShip(i);
		}
		this.fireChange();
	}
	
	public AllShipSettingsPacket.Ship[] getShips() {
		return ships;
	}
	
	public void setPermanantSystemGrid(ShipSystemGrid permanantGrid) {
		setSystemGrid(permanantGrid);
		this.permanantGrid = permanantGrid;
	}
	
	private void fireChange() {
		eventEmitter.emit(Events.CHANGE);
	}
	
	private void restartObjectUpdateTimeout() {
		if (objectUpdateTimeout != null) {
			objectUpdateTimeout.cancel(true);
			objectUpdateTimeout = null;
		}
		objectUpdateTimeout = scheduler.schedule(() -> { 
			System.out.println("Object update timeout");
			gameOverScreen = true;
			fireChange();
		}, 5, TimeUnit.SECONDS);
	}
		
	public enum Events {
		CHANGE
	}
}
