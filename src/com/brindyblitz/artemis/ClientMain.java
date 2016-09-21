package com.brindyblitz.artemis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.FakeEngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.RealEngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.ui.UserInterfaceFrame;

public class ClientMain {
	
	public static void main(String[] args) throws IOException {
		String host = null;
		Integer port = null;

		List<String> argList = new ArrayList<>(Arrays.asList(args));
		
		boolean proxy = false;
		boolean fake = false;
		if (argList.contains("--proxy")) {
			proxy = true;
			argList.remove("--proxy");
		}
		if (argList.contains("--fake")) {
			fake = true;
			argList.remove("--fake");
		}
		
		if (proxy && fake) {
			System.err.println("Cannot use proxy and fake modes together");
			printUsage();
			return;
		}
		
		if (!fake && argList.size() > 2 || fake && argList.size() > 0) {
			System.err.println("Incorrect number of arguments (" + args.length + ")");
			printUsage();
			return;
		}
		
		if (argList.size() > 0) {
			host = argList.get(0);			
			if (argList.size() == 2) {
				try {
					port = Integer.parseInt(argList.get(1));
				} catch (NumberFormatException e) {
					System.err.println("Unable to parse port: '" + args[1] + "'!");
					printUsage();
					return;
				}
			}
		}
		
		new ClientMain(host, port, proxy, fake);
	}

	public ClientMain(String host, Integer port, boolean proxy, boolean fake) throws IOException {
		EngineeringConsoleManager engineeringConsoleManager;
		if (fake) {
			engineeringConsoleManager = new FakeEngineeringConsoleManager();
		}
		else {
			engineeringConsoleManager = new RealEngineeringConsoleManager(proxy);
			if (host != null) {
				if (port != null) {
					engineeringConsoleManager.connect(host, port);
				}
				else {
					engineeringConsoleManager.connect(host);
				}
			}
		}
		
		buildUIFrame(engineeringConsoleManager, host);		
	}

	private static UserInterfaceFrame buildUIFrame(EngineeringConsoleManager engineeringConsoleManager, String host) {
		UserInterfaceFrame userInterfaceFrame = new UserInterfaceFrame(engineeringConsoleManager, host);
		userInterfaceFrame.setVisible(true);
		return userInterfaceFrame;
	}

	private static void printUsage() {
		System.err.println("\nUsage:\n" +
				"  Normal Mode\n" +
				"    engineeringClient [<host> [<port]]\n" +
				"  Proxy Mode (a vanilla client must connect to this client and data can be seen on both clients)\n" +
				"    engineeringClient --proxy <host> [<port]\n" +
				"  Fake Mode (the client is set to local debug mode and where no network activity occurs)\n" +
				"    engineeringClient --fake\n"
				);
	}
}
