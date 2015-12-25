package com.brindyblitz.artemis;

import java.io.IOException;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.RealEngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.ui.UserInterfaceFrame;
import com.brindyblitz.artemis.protocol.WorldAwareRobustProxyListener;

public class ClientMain {
    public static void main(String[] args) throws IOException {

        String host = "192.168.1.116"; //args[0];
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 2010;
     
        new ClientMain(host, port);
    }


    public ClientMain(String host, int port) throws IOException {
    	/* Comment next two lines and uncomment the third line to test the UI without an actual server */
    	
    	WorldAwareRobustProxyListener worldAwareRobustProxyListener = new WorldAwareRobustProxyListener(host, port, port);
    	EngineeringConsoleManager engineeringConsoleManager = new RealEngineeringConsoleManager(worldAwareRobustProxyListener);
//    	EngineeringConsoleManager engineeringConsoleManager = new FakeEngineeringConsoleManager();
        
        new UserInterfaceFrame(engineeringConsoleManager).setVisible(true);
    }
}
