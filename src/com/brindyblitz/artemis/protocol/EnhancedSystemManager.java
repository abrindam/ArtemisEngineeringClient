package com.brindyblitz.artemis.protocol;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.brindyblitz.artemis.utils.EventEmitter;
import com.brindyblitz.artemis.utils.EventSubscriber;
import com.walkertribe.ian.iface.Listener;
import com.walkertribe.ian.protocol.core.GameOverPacket;
import com.walkertribe.ian.protocol.core.eng.EngGridUpdatePacket;
import com.walkertribe.ian.protocol.core.world.DestroyObjectPacket;
import com.walkertribe.ian.protocol.core.world.IntelPacket;
import com.walkertribe.ian.protocol.core.world.ObjectUpdatePacket;
import com.walkertribe.ian.util.ShipSystemGrid;
import com.walkertribe.ian.world.SystemManager;

public class EnhancedSystemManager extends SystemManager {

	private EventEmitter<Events> eventEmitter = new EventEmitter<>();
	public final EventSubscriber<Events> events = eventEmitter.subscriber;
	private final ScheduledExecutorService scheduler;
	private boolean gameOverScreen;
	private ScheduledFuture<?> objectUpdateTimeout = null;
	private ShipSystemGrid permanantGrid;
	
	public EnhancedSystemManager() {
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
	public void onCompletelyGameOver(GameOverPacket pkt) {
		this.gameOverScreen = false;
		this.clear();
		if (permanantGrid != null) {
			setSystemGrid(permanantGrid);
		}
		fireChange();
	}
	
	public boolean isGameOverScreen() {
		return gameOverScreen;
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
