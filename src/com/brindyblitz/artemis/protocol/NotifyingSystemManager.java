package com.brindyblitz.artemis.protocol;

import java.util.ArrayList;
import java.util.List;

import net.dhleong.acl.iface.Listener;
import net.dhleong.acl.protocol.core.eng.EngGridUpdatePacket;
import net.dhleong.acl.protocol.core.world.DestroyObjectPacket;
import net.dhleong.acl.protocol.core.world.IntelPacket;
import net.dhleong.acl.protocol.core.world.ObjectUpdatePacket;
import net.dhleong.acl.world.SystemManager;

public class NotifyingSystemManager extends SystemManager {

	private List<SystemManagerChangeListener> listeners = new ArrayList<>();
	
	public void addChangeListener(SystemManagerChangeListener listener) {
		this.listeners.add(listener);
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
		for (SystemManagerChangeListener listener: listeners) {
			listener.onChange();
		}
	}
	
	public static interface SystemManagerChangeListener {
		public void onChange();
	}
}
