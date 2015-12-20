package com.brindybliz.artemis;

public class WorldAwareRobustProxyListener extends RobustProxyListener {

	private NotifyingSystemManager systemManager;

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
}
