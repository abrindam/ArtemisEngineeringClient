package com.brindyblitz.artemis.protocol;

import com.brindyblitz.artemis.utils.EventEmitter;

public class WorldAwareRobustProxyListener extends RobustProxyListener implements WorldAwareServer {

	private NotifyingSystemManager systemManager;
	private EventEmitter<Events> eventEmitter = new EventEmitter<>();

	public WorldAwareRobustProxyListener(String serverAddr, int serverPort, int proxyPort) {
		super(serverAddr, serverPort, proxyPort);
		this.systemManager = new NotifyingSystemManager();
	}
	
	@Override
	protected void onBeforeClientServerStart() {		
		this.getServer().addListener(this.systemManager);
	}
	
	public NotifyingSystemManager getSystemManager() {
		return systemManager;
	}
	
	@Override
	protected void onConnected() {
		eventEmitter.emit(Events.CONNECTION_STATE_CHANGE);
	}
	
	public void onEvent(Events event, Runnable listener) {
		eventEmitter.on(event, listener);
	};
}
