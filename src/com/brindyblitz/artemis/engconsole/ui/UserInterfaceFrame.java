package com.brindyblitz.artemis.engconsole.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import javax.swing.JFrame;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.config.InputMapping;

import net.dhleong.acl.enums.ShipSystem;

import static net.dhleong.acl.enums.ShipSystem.*;

public class UserInterfaceFrame extends JFrame implements KeyListener{

	private static final long serialVersionUID = 1L;
	private EngineeringConsoleManager engineeringConsoleManager;
	private int numSliders = 0;
	private long lastResetEnergy;

	private static final int
            SLIDER_OFFSET_MULTIPLIER = 125,
            SLIDER_OFFSET_ADDITIONAL = 25,
            MAIN_SLIDER_Y = 200,
            HEAT_SLIDER_Y = 150,
            HEALTH_SLIDER_Y = 120;

	private InputManager inputManager;
	private PresetManager presetManager;

    private static HashMap<ShipSystem, String> SYSTEM_NAME_MAP = new HashMap<ShipSystem, String>();

	public UserInterfaceFrame(EngineeringConsoleManager engineeringConsoleManager) {
		this.engineeringConsoleManager = engineeringConsoleManager;
		setTitle("Artemis: Engineering Console (Client)");
		setSize(1024, 768);
		getContentPane().setBackground(Color.BLACK);
		setLayout(null);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

        SYSTEM_NAME_MAP.put(BEAMS, "Primary Beam");
        SYSTEM_NAME_MAP.put(TORPEDOES, "Torpedoes");
        SYSTEM_NAME_MAP.put(SENSORS, "Sensors");
        SYSTEM_NAME_MAP.put(MANEUVERING, "Maneuver");
        SYSTEM_NAME_MAP.put(IMPULSE, "Impulse");
        SYSTEM_NAME_MAP.put(WARP_JUMP_DRIVE, "Warp");
        SYSTEM_NAME_MAP.put(FORE_SHIELDS, "Front Shield");
        SYSTEM_NAME_MAP.put(AFT_SHIELDS, "Rear Shield");

        this.inputManager = new InputManager();
        this.presetManager = new PresetManager(engineeringConsoleManager);
        this.addKeyListener(this);

        for (ShipSystem system : ShipSystem.values()) {
            InputMapping mapping = this.inputManager.mappings.get(system);
            this.addSlider(mapping.system, SYSTEM_NAME_MAP.get(mapping.system), mapping);
        }
		
		this.add(new CoolantRemainingSlider(engineeringConsoleManager)).setLocation(50, 570);
	}

	private void addSlider(ShipSystem system, String label, InputMapping mapping) {
		SystemSlider slider = new SystemSlider(system, label, mapping, this.engineeringConsoleManager);
		this.add(slider).setLocation(this.numSliders * SLIDER_OFFSET_MULTIPLIER + SLIDER_OFFSET_ADDITIONAL, MAIN_SLIDER_Y);
		this.addKeyListener(slider);

		SystemHeatSlider systemHeatSlider = new SystemHeatSlider(system, this.engineeringConsoleManager);
		this.add(systemHeatSlider).setLocation(this.numSliders * SLIDER_OFFSET_MULTIPLIER + SLIDER_OFFSET_ADDITIONAL, HEAT_SLIDER_Y);

        SystemHealthSlider systemHealthSlider = new SystemHealthSlider(system, this.engineeringConsoleManager);
        this.add(systemHealthSlider).setLocation(this.numSliders * SLIDER_OFFSET_MULTIPLIER + SLIDER_OFFSET_ADDITIONAL, HEALTH_SLIDER_Y);

		this.numSliders ++;
	}

	@Override
	public void keyPressed(KeyEvent e) {				

		if (e.getKeyCode() == KeyEvent.VK_BACK_SLASH) {
			System.out.println("\n\n\n\n\n\n\n\n");
			System.out.println("Beams: " + this.engineeringConsoleManager.getSystemEnergyAllocated(BEAMS) + "%");
			System.out.println("Torpedoes: " + this.engineeringConsoleManager.getSystemEnergyAllocated(TORPEDOES) + "%");
			System.out.println("Sensors: " + this.engineeringConsoleManager.getSystemEnergyAllocated(SENSORS) + "%");
			System.out.println("Manuvering: " + this.engineeringConsoleManager.getSystemEnergyAllocated(MANEUVERING) + "%");
			System.out.println("Impulse: " + this.engineeringConsoleManager.getSystemEnergyAllocated(IMPULSE) + "%");
			System.out.println("Warp: " + this.engineeringConsoleManager.getSystemEnergyAllocated(WARP_JUMP_DRIVE) + "%");
			System.out.println("Front Shields: " + this.engineeringConsoleManager.getSystemEnergyAllocated(FORE_SHIELDS) + "%");
			System.out.println("Rear Shields: " + this.engineeringConsoleManager.getSystemEnergyAllocated(AFT_SHIELDS) + "%");
		}
		else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			this.engineeringConsoleManager.resetEnergy();
			if (this.lastResetEnergy > System.currentTimeMillis() - 2000) {
				this.engineeringConsoleManager.resetCoolant();
			}
			this.lastResetEnergy = System.currentTimeMillis();
		}
		else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.engineeringConsoleManager.resetCoolant();
		}
		else if (e.getKeyCode() >= KeyEvent.VK_0 && e.getKeyCode() <= KeyEvent.VK_9) {
			int presetNumber = e.getKeyCode() - KeyEvent.VK_0;
			this.presetManager.applyPreset(presetNumber);
		}

	}

    @Override
    public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}
