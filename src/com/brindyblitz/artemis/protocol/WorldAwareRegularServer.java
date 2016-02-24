package com.brindyblitz.artemis.protocol;

import java.io.IOException;

import com.brindyblitz.artemis.utils.EventEmitter;

import net.dhleong.acl.enums.Console;
import net.dhleong.acl.iface.ArtemisNetworkInterface;
import net.dhleong.acl.iface.ConnectionSuccessEvent;
import net.dhleong.acl.iface.Listener;
import net.dhleong.acl.iface.ThreadedArtemisNetworkInterface;
import net.dhleong.acl.protocol.core.setup.ReadyPacket;
import net.dhleong.acl.protocol.core.setup.ReadyPacket2;
import net.dhleong.acl.protocol.core.setup.SetConsolePacket;

public class WorldAwareRegularServer implements WorldAwareServer {

	private ArtemisNetworkInterface server;
	private NotifyingSystemManager systemManager;
	private boolean connected = false;
	private EventEmitter<Events> eventEmitter = new EventEmitter<>();
	
	public WorldAwareRegularServer(String host, int port) throws IOException {
		
		server = new ThreadedArtemisNetworkInterface(host, port, 5*1000);
		
		this.systemManager = new NotifyingSystemManager();
        
		server.addListener(this);
		server.addListener(this.systemManager);
        server.start();
	}
	
	@Listener
    public void onConnectSuccess(ConnectionSuccessEvent event) {
		this.connected  = true;
		System.out.println("Connected!");
		eventEmitter.emit(Events.CONNECTION_STATE_CHANGE);
        server.send(new SetConsolePacket(Console.ENGINEERING, true));
        server.send(new ReadyPacket());
        server.send(new ReadyPacket2());
    }

	@Override
	public NotifyingSystemManager getSystemManager() {
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
