package com.brindyblitz.artemis.protocol;

import com.brindyblitz.artemis.utils.EventEmitter;
import com.walkertribe.ian.Context;

public class WorldAwareRobustProxyListener extends RobustProxyListener implements WorldAwareServer {

	private EnhancedSystemManager systemManager;
	private EventEmitter<Events> eventEmitter = new EventEmitter<>();

	public WorldAwareRobustProxyListener(String serverAddr, int serverPort, int proxyPort, Context context) {
		super(serverAddr, serverPort, proxyPort, context);
		this.systemManager = new EnhancedSystemManager(context);
	}
	
	@Override
	public void ready() {
		// Do nothing, proxied client will send ready		
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
