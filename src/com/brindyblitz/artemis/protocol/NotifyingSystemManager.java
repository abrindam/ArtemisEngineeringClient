package com.brindyblitz.artemis.protocol;

import com.brindyblitz.artemis.utils.EventEmitter;
import com.brindyblitz.artemis.utils.EventSubscriber;

import com.walkertribe.ian.iface.Listener;
import com.walkertribe.ian.protocol.core.eng.EngGridUpdatePacket;
import com.walkertribe.ian.protocol.core.world.DestroyObjectPacket;
import com.walkertribe.ian.protocol.core.world.IntelPacket;
import com.walkertribe.ian.protocol.core.world.ObjectUpdatePacket;
import com.walkertribe.ian.world.SystemManager;

public class NotifyingSystemManager extends SystemManager {

	private EventEmitter<Events> eventEmitter = new EventEmitter<>();
	public final EventSubscriber<Events> events = eventEmitter.subscriber;
	
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
		super.onPacket(pkt);
		this.fireChange();
	}
	
	private void fireChange() {
		eventEmitter.emit(Events.CHANGE);
	}
	
	public enum Events {
		CHANGE
	}
}
