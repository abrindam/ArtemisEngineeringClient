package com.brindyblitz.artemis.engconsole.ui;

import static net.dhleong.acl.enums.ShipSystem.AFT_SHIELDS;
import static net.dhleong.acl.enums.ShipSystem.BEAMS;
import static net.dhleong.acl.enums.ShipSystem.FORE_SHIELDS;
import static net.dhleong.acl.enums.ShipSystem.IMPULSE;
import static net.dhleong.acl.enums.ShipSystem.MANEUVERING;
import static net.dhleong.acl.enums.ShipSystem.SENSORS;
import static net.dhleong.acl.enums.ShipSystem.TORPEDOES;
import static net.dhleong.acl.enums.ShipSystem.WARP_JUMP_DRIVE;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager.EnhancedDamconStatus;
import com.brindyblitz.artemis.engconsole.config.InputMapping;
import com.brindyblitz.artemis.engconsole.ui.damcon.Damcon;

import com.brindyblitz.artemis.utils.AudioManager;
import net.dhleong.acl.enums.ShipSystem;
import net.dhleong.acl.util.GridCoord;

public class InGamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private EngineeringConsoleManager engineeringConsoleManager;
	private int numSliders = 0;
	private long lastResetEnergy;

    private ArrayList<SystemSlider> sliders = new ArrayList<>();
    private Damcon damcon;
	
	private static final int
            SLIDER_OFFSET_MULTIPLIER = 110,
            SLIDER_OFFSET_ADDITIONAL = 25,
            DAMCON_Y = 10,
            PRESET_Y = 50,
            HEALTH_SLIDER_Y = 320,
            HEAT_SLIDER_Y = HEALTH_SLIDER_Y + 30,
            MAIN_SLIDER_Y = HEAT_SLIDER_Y + 40;

	private PresetManager presetManager;

    private static HashMap<ShipSystem, String> SYSTEM_NAME_MAP = new HashMap<ShipSystem, String>();

	public AudioManager audioManager;

	public InGamePanel(EngineeringConsoleManager engineeringConsoleManager, int width, int height) {
		this.setVisible(false);
		this.engineeringConsoleManager = engineeringConsoleManager;

		this.setOpaque(true);
        this.setBackground(Color.BLACK);
		setLayout(null);
		this.setSize(width, height);

        audioManager = new AudioManager(new File(System.getProperty("user.dir"), "assets/sfx").getPath());

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

        for (ShipSystem system : ShipSystem.values()) {
            InputMapping mapping = InputManager.mappings.get(system);
            this.addSlider(mapping.system, SYSTEM_NAME_MAP.get(mapping.system), mapping);
        }

		SystemSlider last_slider = sliders.get(sliders.size() - 1);
		this.add(new CoolantRemainingSlider(engineeringConsoleManager, last_slider.getWidth(), last_slider.getHeight())).setLocation(
				this.numSliders * SLIDER_OFFSET_MULTIPLIER + SLIDER_OFFSET_ADDITIONAL, MAIN_SLIDER_Y);
		
		PresetPanel presetPanel = new PresetPanel(this.presetManager);
		this.add(presetPanel).setLocation(this.getWidth() - presetPanel.getWidth(), PRESET_Y);
		
		this.damcon = new Damcon(engineeringConsoleManager, this.audioManager);
		this.add(damcon.getCanvas()).setLocation(10, DAMCON_Y);

		EnergySlider energy_slider = new EnergySlider(null, engineeringConsoleManager);
        this.add(energy_slider).setLocation(400, 50);
	}

	private void addSlider(ShipSystem system, String label, InputMapping mapping) {
		SystemSlider slider = new SystemSlider(system, label, mapping, this.engineeringConsoleManager, this.audioManager);
		this.add(slider).setLocation(this.numSliders * SLIDER_OFFSET_MULTIPLIER + SLIDER_OFFSET_ADDITIONAL, MAIN_SLIDER_Y);

		SystemHeatSlider systemHeatSlider = new SystemHeatSlider(system, this.engineeringConsoleManager);
		this.add(systemHeatSlider).setLocation(this.numSliders * SLIDER_OFFSET_MULTIPLIER + SLIDER_OFFSET_ADDITIONAL, HEAT_SLIDER_Y);

        SystemHealthSlider systemHealthSlider = new SystemHealthSlider(system, this.engineeringConsoleManager);
        this.add(systemHealthSlider).setLocation(this.numSliders * SLIDER_OFFSET_MULTIPLIER + SLIDER_OFFSET_ADDITIONAL, HEALTH_SLIDER_Y);

        this.sliders.add(slider);
		this.numSliders ++;
	}

	public void handleKeyPress(KeyEvent e) {
        int kc = e.getKeyCode();

		if (kc == KeyEvent.VK_BACK_SLASH) {
			System.out.println("Beams: " + this.engineeringConsoleManager.getSystemEnergyAllocated().get().get(BEAMS) + "%");
			System.out.println("Torpedoes: " + this.engineeringConsoleManager.getSystemEnergyAllocated().get().get(TORPEDOES) + "%");
			System.out.println("Sensors: " + this.engineeringConsoleManager.getSystemEnergyAllocated().get().get(SENSORS) + "%");
			System.out.println("Maneuvering: " + this.engineeringConsoleManager.getSystemEnergyAllocated().get().get(MANEUVERING) + "%");
			System.out.println("Impulse: " + this.engineeringConsoleManager.getSystemEnergyAllocated().get().get(IMPULSE) + "%");
			System.out.println("Warp: " + this.engineeringConsoleManager.getSystemEnergyAllocated().get().get(WARP_JUMP_DRIVE) + "%");
			System.out.println("Front Shields: " + this.engineeringConsoleManager.getSystemEnergyAllocated().get().get(FORE_SHIELDS) + "%");
			System.out.println("Rear Shields: " + this.engineeringConsoleManager.getSystemEnergyAllocated().get().get(AFT_SHIELDS) + "%");
			
//			for (Entry<GridCoord, Float> entry : this.engineeringConsoleManager.getGridHealth().entrySet()) {
//				System.out.println(entry.getKey() + " = " + entry.getValue());
//			}
			
			for (EnhancedDamconStatus damconStatus : this.engineeringConsoleManager.getDamconTeams().get()) {
				System.out.println(damconStatus);
			}
			
			System.out.println("Energy remaining: " + this.engineeringConsoleManager.getTotalEnergyRemaining());

            System.out.println("\n\n\n");
		} else if (kc == KeyEvent.VK_EQUALS) {
			System.out.println("Sending damcon team");
			this.engineeringConsoleManager.moveDamconTeam(0, GridCoord.getInstance(2, 2, 5));
		} else if (kc == KeyEvent.VK_BACK_QUOTE) {
            if (e.isShiftDown()) {
            	if (this.damcon != null) {
            		this.damcon.toggleDamageShake();            		
            	}
            } else {
            	if (this.damcon != null) {
            		this.damcon.startDamageShake(1000l, 0.7d);
            	}
            }
        }
        else if (kc == KeyEvent.VK_SPACE) {
			this.engineeringConsoleManager.resetEnergy();
			if (this.lastResetEnergy > System.currentTimeMillis() - 2000) {
				this.engineeringConsoleManager.resetCoolant();
			}
			this.lastResetEnergy = System.currentTimeMillis();
		}
		else if (kc == KeyEvent.VK_ENTER) {
			this.engineeringConsoleManager.resetCoolant();
		}
		else if (kc >= KeyEvent.VK_0 && kc <= KeyEvent.VK_9) {
			int presetNumber = kc - KeyEvent.VK_0;
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
}
