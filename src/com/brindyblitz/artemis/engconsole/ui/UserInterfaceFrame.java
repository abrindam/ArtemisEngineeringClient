package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager.GameState;

public class UserInterfaceFrame extends JFrame implements KeyListener {

	private static final long serialVersionUID = 1L;
	private EngineeringConsoleManager engineeringConsoleManager;
	private ConnectPanel connectPanel;
	private PreGamePanel preGamePanel;
	private InGamePanel inGamePanel;
	private GameOverPanel gameOverPanel;

	private String host;
	
	private static final int
            WINDOW_WIDTH = 1024,
            WINDOW_HEIGHT = 768;
	private static final Font LABEL_FONT = new Font("Courier New", Font.BOLD, 36);
	private JLabel loading;
            

	public UserInterfaceFrame(EngineeringConsoleManager engineeringConsoleManager, String host) {
		this.engineeringConsoleManager = engineeringConsoleManager;
		this.host = host;
		
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
        setTitle("Artemis: Engineering Console (Client)");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setLayout(null);
		setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        
        this.addKeyListener(this);
        this.setFocusable(true);
		this.setResizable(false);
		
		this.loading = new JLabel("Loading...");
		loading.setForeground(Color.WHITE);
		loading.setFont(LABEL_FONT);
		loading.setHorizontalAlignment(SwingConstants.CENTER);
		loading.setBounds(0, WINDOW_HEIGHT/2 - 40, WINDOW_WIDTH, 50);
		
		getContentPane().add(loading);		
		this.setVisible(true);
		
		updateCurrentPanel();
		
		engineeringConsoleManager.getGameState().onChange(() -> this.updateCurrentPanel());
		
	}
	
	private void updateCurrentPanel() {
		if (engineeringConsoleManager.getGameState().get() == GameState.DISCONNECTED) {
			switchToConnectPanel();
		}
		else if (engineeringConsoleManager.getGameState().get() == GameState.PREGAME) {
			switchToPreGamePanel();
		}
		else if (engineeringConsoleManager.getGameState().get() == GameState.INGAME) {
			switchToInGamePanel();			
		}
		else if (engineeringConsoleManager.getGameState().get() == GameState.GAMEOVER) {
			switchToGameOverPanel();			
		}
//		switchToConnectPanel();
	}
	
	private void switchToConnectPanel() {
		System.out.println("Switch to Connect");
		removeExistingPanels();
		loading.setVisible(true);
		connectPanel = new ConnectPanel(engineeringConsoleManager, WINDOW_WIDTH, WINDOW_HEIGHT, host);
		this.getContentPane().add(connectPanel);
		connectPanel.setVisible(true);
		loading.setVisible(false);
	}
	
	private void switchToPreGamePanel() {
		System.out.println("Switch to PreGame");
		removeExistingPanels();
		loading.setVisible(true);
		preGamePanel = new PreGamePanel(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.getContentPane().add(preGamePanel);
		preGamePanel.setVisible(true);
		loading.setVisible(false);
	}
	
	private void switchToInGamePanel() {
		System.out.println("Switch to InGame");
		removeExistingPanels();
		loading.setVisible(true);
		inGamePanel = new InGamePanel(engineeringConsoleManager, WINDOW_WIDTH, WINDOW_HEIGHT);
		this.getContentPane().add(inGamePanel);
		inGamePanel.setVisible(true);
		loading.setVisible(false);
	}
	
	private void switchToGameOverPanel() {
		System.out.println("Switch to GameOver");
		removeExistingPanels();
		loading.setVisible(true);
		gameOverPanel = new GameOverPanel(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.getContentPane().add(gameOverPanel);
		gameOverPanel.setVisible(true);
		loading.setVisible(false);
	}
	
	private void removeExistingPanels() {
		if (connectPanel != null) {
			this.getContentPane().remove(connectPanel);
			this.connectPanel = null;
		}
		if (inGamePanel != null) {
			inGamePanel.destroy();
			this.getContentPane().remove(inGamePanel);
			this.inGamePanel = null;
		}
		if (preGamePanel != null) {
			this.getContentPane().remove(preGamePanel);
			this.preGamePanel = null;
		}
		if (gameOverPanel != null) {
			this.getContentPane().remove(gameOverPanel);
			this.gameOverPanel = null;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		/***
         * Only one Swing item seems to be able to receive keys at once probably due to the insane Java focus
         * model (see https://docs.oracle.com/javase/7/docs/api/java/awt/doc-files/FocusSpec.html).
         *
         * As such, the UserInterfaceFrame redirects keys to relevant receivers that would normally implement
         * KeyListener.
         */
		if (this.inGamePanel != null) {
			this.inGamePanel.handleKeyPress(e);			
		}
	}

    @Override
    public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}
