package com.brindyblitz.artemis.protocol;

import java.io.IOException;

import com.brindyblitz.artemis.utils.EventEmitter;
import com.walkertribe.ian.enums.Console;
import com.walkertribe.ian.iface.ArtemisNetworkInterface;
import com.walkertribe.ian.iface.ConnectionSuccessEvent;
import com.walkertribe.ian.iface.Listener;
import com.walkertribe.ian.iface.ThreadedArtemisNetworkInterface;
import com.walkertribe.ian.protocol.core.TogglePerspectivePacket;
import com.walkertribe.ian.protocol.core.eng.EngSetAutoDamconPacket;
import com.walkertribe.ian.protocol.core.setup.ReadyPacket;
import com.walkertribe.ian.protocol.core.setup.ReadyPacket2;
import com.walkertribe.ian.protocol.core.setup.SetConsolePacket;

public class WorldAwareRegularServer implements WorldAwareServer {

	private ArtemisNetworkInterface server;
	private EnhancedSystemManager systemManager;
	private boolean connected = false;
	private EventEmitter<Events> eventEmitter = new EventEmitter<>();
	
	public WorldAwareRegularServer(String host, int port) throws IOException {
		
		server = new ThreadedArtemisNetworkInterface(host, port, 5*1000);
		
		this.systemManager = new EnhancedSystemManager();
        
		server.addListener(this);
		server.addListener(this.systemManager);
        server.start();
	}
	
	public void disconnect() {
		server.stop();
		this.connected  = false;
		eventEmitter.emit(Events.CONNECTION_STATE_CHANGE);
	}
	
	@Listener
    public void onConnectSuccess(ConnectionSuccessEvent event) {
		this.connected  = true;
		System.out.println("Connected!");
		eventEmitter.emit(Events.CONNECTION_STATE_CHANGE);
        server.send(new SetConsolePacket(Console.ENGINEERING, true));
        server.send(new ReadyPacket());
        server.send(new ReadyPacket2());
        server.send(new TogglePerspectivePacket());
        server.send(new EngSetAutoDamconPacket(true));
    }

	@Override
	public EnhancedSystemManager getSystemManager() {
		return this.systemManager;
	}

	@Override
	public ArtemisNetworkInterface getServer() {
		return this.server;
	}

	@Override
	public boolean isConnected() {
		return this.connected;
	}

	@Override
	public void onEvent(Events event, Runnable listener) {
		eventEmitter.on(event,  listener);		
	}
}
