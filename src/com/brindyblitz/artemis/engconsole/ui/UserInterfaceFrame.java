package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager.Events;

public class UserInterfaceFrame extends JFrame implements KeyListener {

	private static final long serialVersionUID = 1L;
	private EngineeringConsoleManager engineeringConsoleManager;
	private InGamePanel inGamePanel;
	
	private static final int
            WINDOW_WIDTH = 1024,
            WINDOW_HEIGHT = 768;
	private static final Font LABEL_FONT = new Font("Courier New", Font.BOLD, 36);
	private JLabel loading;
            

	public UserInterfaceFrame(EngineeringConsoleManager engineeringConsoleManager) {
		this.engineeringConsoleManager = engineeringConsoleManager;
		
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
		
		engineeringConsoleManager.onEvent(Events.GAME_STATE_CHANGE, () -> {
			switchToInGamePanel();
		});
	}
	
	private void switchToInGamePanel() {
		if (inGamePanel != null) {
			this.getContentPane().remove(inGamePanel);
		}
		loading.setVisible(true);
		inGamePanel = new InGamePanel(engineeringConsoleManager, WINDOW_WIDTH, WINDOW_HEIGHT);
		this.getContentPane().add(inGamePanel);
		inGamePanel.setVisible(true);
		loading.setVisible(false);
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
        this.inGamePanel.handleKeyPress(e);
	}

    @Override
    public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}
