package com.brindyblitz.artemis.protocol;

import com.walkertribe.ian.iface.ArtemisNetworkInterface;

public interface WorldAwareServer {

	EnhancedSystemManager getSystemManager();
	ArtemisNetworkInterface getServer();
	boolean isConnected();
	void onEvent(Events event, Runnable listener);
	
	public enum Events {
		CONNECTION_STATE_CHANGE
	}

}
