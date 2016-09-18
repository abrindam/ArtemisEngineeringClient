package com.brindyblitz.artemis.protocol;

import com.brindyblitz.artemis.utils.EventEmitter;

public class WorldAwareRobustProxyListener extends RobustProxyListener implements WorldAwareServer {

	private EnhancedSystemManager systemManager;
	private EventEmitter<Events> eventEmitter = new EventEmitter<>();

	public WorldAwareRobustProxyListener(String serverAddr, int serverPort, int proxyPort) {
		super(serverAddr, serverPort, proxyPort);
		this.systemManager = new EnhancedSystemManager();
	}
	
	@Override
	public void disconnect() {
		super.disconnect();
		eventEmitter.emit(Events.CONNECTION_STATE_CHANGE);
	}
	
	@Override
	protected void onBeforeClientServerStart() {		
		this.getServer().addListener(this.systemManager);
	}
	
	public EnhancedSystemManager getSystemManager() {
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
