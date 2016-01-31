package com.brindyblitz.artemis.protocol;

import com.brindyblitz.artemis.utils.EventEmitter;

import net.dhleong.acl.iface.Listener;
import net.dhleong.acl.protocol.core.eng.EngGridUpdatePacket;
import net.dhleong.acl.protocol.core.world.DestroyObjectPacket;
import net.dhleong.acl.protocol.core.world.IntelPacket;
import net.dhleong.acl.protocol.core.world.ObjectUpdatePacket;
import net.dhleong.acl.world.SystemManager;

public class NotifyingSystemManager extends SystemManager {

	private static final Object CHANGE_EVENT = new Object();
	private EventEmitter<Object> eventEmitter = new EventEmitter<>();
	
	public void addChangeListener(Runnable listener) {
		eventEmitter.on(CHANGE_EVENT, listener);
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
		super.onPacket(pkt);
		this.fireChange();
	}
	
	private void fireChange() {
		eventEmitter.emit(CHANGE_EVENT);
	}
	
	public static interface SystemManagerChangeListener {
		public void onChange();
	}
}
