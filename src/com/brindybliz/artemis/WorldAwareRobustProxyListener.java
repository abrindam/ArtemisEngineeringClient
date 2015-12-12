package com.brindybliz.artemis;

import net.dhleong.acl.world.SystemManager;

public class WorldAwareRobustProxyListener extends RobustProxyListener {

	private SystemManager systemManager;

	public WorldAwareRobustProxyListener(String serverAddr, int serverPort, int proxyPort) {
		super(serverAddr, serverPort, proxyPort);
	}
	
	@Override
	protected void onBeforeClientServerStart() {
		this.systemManager = new SystemManager();
		this.getServer().addListener(this.systemManager);
	}
	
	public SystemManager getSystemManager() {
		return systemManager;
	}
}
