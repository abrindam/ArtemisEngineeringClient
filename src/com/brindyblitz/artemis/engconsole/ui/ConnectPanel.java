package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager.GameState;

public class ConnectPanel extends TransparentJPanel {

	private static final long serialVersionUID = 1L;
	private static final Font LABEL_FONT = new Font("Courier New", Font.BOLD, 24);
	private static final Font BUTTON_FONT = new Font("Courier New", Font.BOLD, 16);
	private static final Font ERROR_FONT = new Font("Courier New", Font.BOLD, 16);
	private JLabel prompt;
	private JTextField input;
	private JButton submit;
	private JLabel error;
	private ExecutorService executor;
	

	public ConnectPanel(EngineeringConsoleManager engineeringConsoleManager, int width, int height) {
		executor = Executors.newSingleThreadExecutor();
		this.setVisible(false);
		this.setBounds(0, 0, width, height);
		this.setLayout(null);
		this.setBackground(Color.BLACK);
		
		this.prompt = new JLabel("Enter Server Address");
		prompt.setForeground(Color.WHITE);
		prompt.setFont(LABEL_FONT);
		prompt.setHorizontalAlignment(SwingConstants.CENTER);
		prompt.setBounds(0, height/2 - 80, width, 50);
		this.add(prompt);
		
		this.input = new JTextField();
		input.setBounds(width/2 - 180/2, height/2 - 20, 180, 30);
		input.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(input);
		
		this.submit = new JButton("Connect");
		submit.setBounds(width/2 - 110/2, height/2 + 30, 110, 30);
		submit.setHorizontalAlignment(SwingConstants.CENTER);
		submit.setBackground(Color.BLACK);
		submit.setForeground(Color.WHITE);
		submit.setBorder(new LineBorder(Color.GREEN));
		submit.setFocusPainted(false);
		submit.setFont(BUTTON_FONT);
		
		this.error = new JLabel("Failed to connect");
		error.setForeground(Color.RED);
		error.setFont(ERROR_FONT);
		error.setHorizontalAlignment(SwingConstants.CENTER);
		error.setBounds(0, height/2 + 50, width, 50);
		error.setVisible(false);
		this.add(error);
		
		
		
		submit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				error.setVisible(false);
				executor.submit(() -> {
					engineeringConsoleManager.connect(input.getText());
					if (engineeringConsoleManager.getGameState().get() == GameState.DISCONNECTED) {
						error.setVisible(true);
					}
				});
			}
		});
		this.add(submit);
	}
}
