package com.brindyblitz.artemis.protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import net.dhleong.acl.enums.ConnectionType;
import net.dhleong.acl.iface.ArtemisNetworkInterface;
import net.dhleong.acl.iface.BaseDebugger;
import net.dhleong.acl.iface.DisconnectEvent;
import net.dhleong.acl.iface.Listener;
import net.dhleong.acl.iface.ThreadedArtemisNetworkInterface;
import net.dhleong.acl.protocol.ArtemisPacket;
import net.dhleong.acl.protocol.RawPacket;
import net.dhleong.acl.protocol.UnparsedPacket;

public class RobustProxyListener implements Runnable {

	private String serverAddr;
	private int serverPort;
	private int proxyPort;

	private ArtemisNetworkInterface server;
	private ArtemisNetworkInterface client;

	public RobustProxyListener(String serverAddr, int serverPort, int proxyPort) {
		this.serverAddr = serverAddr;
		this.serverPort = serverPort;
		this.proxyPort = proxyPort;
		new Thread(this).start();
	}

	@Override
	public void run() {
		ServerSocket listener = null;

		try {
			listener = new ServerSocket(this.proxyPort, 0);
			listener.setSoTimeout(0);

			System.out.println("Listening for connections on port " + this.proxyPort + "...");
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

			this.server.start();
			this.client.start();
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

	public ArtemisNetworkInterface getServer() {
		return server;
	}

	public ArtemisNetworkInterface getClient() {
		return client;
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


	private void proxyPacket(ArtemisPacket pkt) {
		ConnectionType type = pkt.getConnectionType();
		ArtemisNetworkInterface dest = type == ConnectionType.SERVER ? client : server;
		dest.send(pkt);
		//        System.out.println(type + "> " + pkt.getType() + "-" + pkt);
	}

	private class InternalDebugger extends BaseDebugger {

		private ThreadLocal<UnparsedPacket> pktToProxy = new ThreadLocal<>();

		@Override
		public void onRecvPacketBytes(ConnectionType connType, int pktType, byte[] payload) {
			this.pktToProxy.set(new UnparsedPacket(connType, pktType, payload));
		}

		@Override
		public void onRecvUnparsedPacket(RawPacket pkt) {
			RobustProxyListener.this.proxyPacket(pkt);
		}

		@Override
		public void onRecvParsedPacket(ArtemisPacket pkt) {
			if (RobustProxyListener.this.shouldProxyPacket(pkt)) {
				RobustProxyListener.this.proxyPacket(pktToProxy.get());
			}
		}
	}

	protected boolean shouldProxyPacket(ArtemisPacket packet) {
		return true;
	}
}
