package com.brindyblitz.artemis.protocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.walkertribe.ian.enums.ConnectionType;
import com.walkertribe.ian.iface.ArtemisNetworkInterface;
import com.walkertribe.ian.iface.BaseDebugger;
import com.walkertribe.ian.iface.DisconnectEvent;
import com.walkertribe.ian.iface.Listener;
import com.walkertribe.ian.iface.ThreadedArtemisNetworkInterface;
import com.walkertribe.ian.protocol.ArtemisPacket;
import com.walkertribe.ian.protocol.RawPacket;
import com.walkertribe.ian.protocol.UnparsedPacket;

public class RobustProxyListener implements Runnable {

	private String serverAddr;
	private int serverPort;
	private int proxyPort;
	private boolean connected;

	private ArtemisNetworkInterface server;
	private ArtemisNetworkInterface client;

	public RobustProxyListener(String serverAddr, int serverPort, int proxyPort) {
		this.serverAddr = serverAddr;
		this.serverPort = serverPort;
		this.proxyPort = proxyPort;
		new Thread(this).start();
	}
	
	public void disconnect() {
		this.server.stop();
		this.client.stop();
		this.connected = true;
	}

	@Override
	public void run() {
		ServerSocket listener = null;

		try {
			listener = new ServerSocket(this.proxyPort, 0);
			listener.setSoTimeout(0);

			System.out.println("Listening for connections at " + InetAddress.getLocalHost().getHostAddress() + ":" + this.proxyPort);
			Socket skt = listener.accept();

			System.out.println("Received connection from " + skt.getRemoteSocketAddress().toString().substring(1) + ".");
			this.client = new ThreadedArtemisNetworkInterface(skt, ConnectionType.CLIENT);

			System.out.print("Connecting to server at " + serverAddr + ":" + serverPort + "...");
			this.server = new ThreadedArtemisNetworkInterface(serverAddr, serverPort);


			this.server.attachDebugger(new InternalDebugger());
			this.client.attachDebugger(new InternalDebugger());

			this.server.addListener(this);
			this.client.addListener(this);
			this.onBeforeClientServerStart();
			this.connected = true;

			this.server.start();
			this.client.start();
			this.onConnected();
			System.out.println("connection established.");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (listener != null && !listener.isClosed()) {
				try {
					listener.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	protected void onBeforeClientServerStart() {

	}
	
	protected void onConnected() {
		
	}

	public ArtemisNetworkInterface getServer() {
		return server;
	}

	public ArtemisNetworkInterface getClient() {
		return client;
	}
	
	public boolean isConnected() {
		return connected;
	}

	@Listener
	public void onDisconnect(DisconnectEvent event) {
		server.stop();
		client.stop();
		System.out.println("Disconnect: " + event);

		if (event.getException() != null) {
			event.getException().printStackTrace();
		}
	}


	private void proxyPacket(ArtemisPacket pkt, ArtemisPacket parsedPacket) {
		ConnectionType type = pkt.getConnectionType();
		ArtemisNetworkInterface dest = type == ConnectionType.SERVER ? client : server;
		dest.send(pkt);
		ArtemisPacket debugPacket = parsedPacket == null ? pkt : parsedPacket;
		if (! debugPacket.toString().equals("0x80803df9 00000000")) { // ignore empty ObjectUpdatePacket(s)
			System.out.println(System.currentTimeMillis() + " " + type + "> " + debugPacket);
		}
	}

	private class InternalDebugger extends BaseDebugger {

		private ThreadLocal<UnparsedPacket> pktToProxy = new ThreadLocal<>();

		@Override
		public void onRecvPacketBytes(ConnectionType connType, int pktType, byte[] payload) {
			this.pktToProxy.set(new UnparsedPacket(connType, pktType, payload));
		}

		@Override
		public void onRecvUnparsedPacket(RawPacket pkt) {
			RobustProxyListener.this.proxyPacket(pkt, null);
		}

		@Override
		public void onRecvParsedPacket(ArtemisPacket pkt) {
			if (RobustProxyListener.this.shouldProxyPacket(pkt)) {
				RobustProxyListener.this.proxyPacket(pktToProxy.get(), pkt);
			}
		}
	}

	protected boolean shouldProxyPacket(ArtemisPacket packet) {
		return true;
	}
}
