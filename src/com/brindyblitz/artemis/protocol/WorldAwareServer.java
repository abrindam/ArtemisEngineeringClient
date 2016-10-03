package com.brindyblitz.artemis.protocol;

import com.walkertribe.ian.iface.ArtemisNetworkInterface;

public interface WorldAwareServer {

	EnhancedSystemManager getSystemManager();
	ArtemisNetworkInterface getServer();
	boolean isConnected();
	void onEvent(Events event, Runnable listener);
	void ready();
	void disconnect();
	
	public enum Events {
		CONNECTION_STATE_CHANGE
	}

}
