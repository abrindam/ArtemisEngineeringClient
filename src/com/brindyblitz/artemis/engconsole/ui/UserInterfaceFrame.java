package com.brindyblitz.artemis.engconsole.ui;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.config.InputMapping;
import net.dhleong.acl.enums.ShipSystem;

import javax.media.j3d.Canvas3D;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

import static net.dhleong.acl.enums.ShipSystem.*;

public class UserInterfaceFrame extends JFrame implements KeyListener {

	private static final long serialVersionUID = 1L;
	private EngineeringConsoleManager engineeringConsoleManager;
	private int numSliders = 0;
	private long lastResetEnergy;

    private ArrayList<SystemSlider> sliders = new ArrayList<>();
	private Canvas3D damconCanvas;

    // TODO: constant cleanup!
	private static final int
            WINDOW_WIDTH = 1024,
            WINDOW_HEIGHT = 768,
            SLIDER_OFFSET_MULTIPLIER = 110,
            SLIDER_OFFSET_ADDITIONAL = 25,
            DAMCON_Y = 10,
            HEALTH_SLIDER_Y = 320,
            HEAT_SLIDER_Y = HEALTH_SLIDER_Y + 30,
            MAIN_SLIDER_Y = HEAT_SLIDER_Y + 40;

	private PresetManager presetManager;

    private static HashMap<ShipSystem, String> SYSTEM_NAME_MAP = new HashMap<ShipSystem, String>();

	public UserInterfaceFrame(EngineeringConsoleManager engineeringConsoleManager, Canvas3D damcon_canvas) {
		this.engineeringConsoleManager = engineeringConsoleManager;
        this.damconCanvas = damcon_canvas;

        setTitle("Artemis: Engineering Console (Client)");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
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

        InputManager.init();
        this.presetManager = new PresetManager(engineeringConsoleManager);
        this.addKeyListener(this);

        for (ShipSystem system : ShipSystem.values()) {
            InputMapping mapping = InputManager.mappings.get(system);
            this.addSlider(mapping.system, SYSTEM_NAME_MAP.get(mapping.system), mapping);
        }

		SystemSlider last_slider = sliders.get(sliders.size() - 1);
		this.add(new CoolantRemainingSlider(engineeringConsoleManager, last_slider.getWidth(), last_slider.getHeight())).setLocation(
				this.numSliders * SLIDER_OFFSET_MULTIPLIER + SLIDER_OFFSET_ADDITIONAL, MAIN_SLIDER_Y);

        this.getContentPane().add(damconCanvas).setLocation(10, DAMCON_Y);

        this.setFocusable(true);
	}

	private void addSlider(ShipSystem system, String label, InputMapping mapping) {
		SystemSlider slider = new SystemSlider(system, label, mapping, this.engineeringConsoleManager);
		this.add(slider).setLocation(this.numSliders * SLIDER_OFFSET_MULTIPLIER + SLIDER_OFFSET_ADDITIONAL, MAIN_SLIDER_Y);

		SystemHeatSlider systemHeatSlider = new SystemHeatSlider(system, this.engineeringConsoleManager);
		this.add(systemHeatSlider).setLocation(this.numSliders * SLIDER_OFFSET_MULTIPLIER + SLIDER_OFFSET_ADDITIONAL, HEAT_SLIDER_Y);

        SystemHealthSlider systemHealthSlider = new SystemHealthSlider(system, this.engineeringConsoleManager);
        this.add(systemHealthSlider).setLocation(this.numSliders * SLIDER_OFFSET_MULTIPLIER + SLIDER_OFFSET_ADDITIONAL, HEALTH_SLIDER_Y);

        this.sliders.add(slider);
		this.numSliders ++;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_BACK_SLASH) {
			System.out.println("Beams: " + this.engineeringConsoleManager.getSystemEnergyAllocated(BEAMS) + "%");
			System.out.println("Torpedoes: " + this.engineeringConsoleManager.getSystemEnergyAllocated(TORPEDOES) + "%");
			System.out.println("Sensors: " + this.engineeringConsoleManager.getSystemEnergyAllocated(SENSORS) + "%");
			System.out.println("Manuvering: " + this.engineeringConsoleManager.getSystemEnergyAllocated(MANEUVERING) + "%");
			System.out.println("Impulse: " + this.engineeringConsoleManager.getSystemEnergyAllocated(IMPULSE) + "%");
			System.out.println("Warp: " + this.engineeringConsoleManager.getSystemEnergyAllocated(WARP_JUMP_DRIVE) + "%");
			System.out.println("Front Shields: " + this.engineeringConsoleManager.getSystemEnergyAllocated(FORE_SHIELDS) + "%");
			System.out.println("Rear Shields: " + this.engineeringConsoleManager.getSystemEnergyAllocated(AFT_SHIELDS) + "%");

            System.out.println("\n\n\n");
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
		} else {
            /***
             * Only one Swing item seems to be able to receive keys at once probably due to the insane Java focus
             * model (see https://docs.oracle.com/javase/7/docs/api/java/awt/doc-files/FocusSpec.html).
             *
             * As such, the UserInterfaceFrame redirects keys to relevant receivers that would normally implement
             * KeyListener.
             */

            for (SystemSlider slider : this.sliders) {
                slider.handleKeyPress(e);
            }
        }
	}

    @Override
    public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}
