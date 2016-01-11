package com.brindyblitz.artemis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.FakeEngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.RealEngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.ui.UserInterfaceFrame;
import com.brindyblitz.artemis.protocol.WorldAwareRobustProxyListener;
import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class ClientMain {
	public static void main(String[] args) throws IOException {
		String host = "localhost";
		int port = 2010;

		if (args.length > 2) {
			System.err.println("Incorrect number of arguments (" + args.length + ")");
			printUsage();
			return;
		}

		if (args.length == 0) {
			new ClientMain();
		} else {

			if (args.length >= 1) {
				host = args[0];
			}
			if (args.length == 2) {
				try {
					port = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					System.err.println("Unable to parse port: '" + args[1] + "'!");
					printUsage();
					return;
				}
			}

			new ClientMain(host, port);
		}
	}

	public ClientMain() {
		EngineeringConsoleManager engineeringConsoleManager = new FakeEngineeringConsoleManager();
		buildUIFrame(engineeringConsoleManager);
		String OBJ_PATH = new File(System.getProperty("user.dir"), "art/models/obj-from-blender/artemis2.obj").getPath();
		
		SimpleUniverse universe = new SimpleUniverse();		
		universe.getViewingPlatform().setNominalViewingTransform();
		try {
			Scene scene = new ObjectFile(ObjectFile.RESIZE).load(OBJ_PATH);
			universe.addBranchGraph(scene.getSceneGroup());
		} catch (FileNotFoundException | IncorrectFormatException | ParsingErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ClientMain(String host, int port) throws IOException {
		WorldAwareRobustProxyListener worldAwareRobustProxyListener = new WorldAwareRobustProxyListener(host, port, port);
		EngineeringConsoleManager engineeringConsoleManager = new RealEngineeringConsoleManager(worldAwareRobustProxyListener);
		buildUIFrame(engineeringConsoleManager);
	}

	private static void buildUIFrame(EngineeringConsoleManager engineeringConsoleManager) {
		new UserInterfaceFrame(engineeringConsoleManager).setVisible(true);
	}

	private static void printUsage() {
		System.out.println("Either:\n" +
				"Zero arguments: the client is set to local debug mode and where no network activity occurs\n" +
				"Two arguments: <host> <port> (where host is either a hostname, e.g. 'localhost', or an IP address, e.g. '192.168.1.5')");
	}
}
