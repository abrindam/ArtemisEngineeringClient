package com.brindybliz.artemis;

import java.io.IOException;

import net.dhleong.acl.enums.Console;
import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.iface.ArtemisNetworkInterface;
import net.dhleong.acl.iface.ConnectionSuccessEvent;
import net.dhleong.acl.iface.DisconnectEvent;
import net.dhleong.acl.iface.Listener;
import net.dhleong.acl.iface.ThreadedArtemisNetworkInterface;
import net.dhleong.acl.protocol.core.eng.EngSetEnergyPacket;
import net.dhleong.acl.protocol.core.setup.ReadyPacket;
import net.dhleong.acl.protocol.core.setup.SetConsolePacket;
import net.dhleong.acl.world.SystemManager;

public class ClientMain {
    public static void main(String[] args) {
//        if (args.length == 0) {
//            System.out.println("Usage: ClientDemo {host} [port]");
//            return;
//        }

        String host = "192.168.1.100"; //args[0];
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 2010;

        try {
            new ClientMain(host, port);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private ArtemisNetworkInterface server;

    public ClientMain(String host, int port) throws IOException {
        server = new ThreadedArtemisNetworkInterface(host, port);
        SystemManager systemManager = new SystemManager();
        server.addListener(this);
        server.addListener(systemManager);
        server.start();
        
        new UserInterface(server, systemManager).setVisible(true);;
    }

    @Listener
    public void onConnectSuccess(ConnectionSuccessEvent event) {
        server.send(new SetConsolePacket(Console.ENGINEERING, true));
        server.send(new ReadyPacket());
        
        System.out.println("Connected to server");
    }

//    @Listener
//    public void onPacket(ArtemisPacket pkt) {
//        System.out.println(pkt);
//    }

    @Listener
    public void onDisconnect(DisconnectEvent event) {
        System.out.println("Disconnected: " + event.getCause());
    }
}
