package com.brindyblitz.artemis.protocol;

import net.dhleong.acl.iface.ArtemisNetworkInterface;

public interface WorldAwareServer {

	NotifyingSystemManager getSystemManager();
	ArtemisNetworkInterface getServer();
	boolean isConnected();
	void onEvent(Events event, Runnable listener);
	
	public enum Events {
		CONNECTION_STATE_CHANGE
	}

}
