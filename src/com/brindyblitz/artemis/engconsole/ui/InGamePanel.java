package com.brindyblitz.artemis.engconsole.ui;

import static com.walkertribe.ian.enums.ShipSystem.AFT_SHIELDS;
import static com.walkertribe.ian.enums.ShipSystem.BEAMS;
import static com.walkertribe.ian.enums.ShipSystem.FORE_SHIELDS;
import static com.walkertribe.ian.enums.ShipSystem.IMPULSE;
import static com.walkertribe.ian.enums.ShipSystem.MANEUVERING;
import static com.walkertribe.ian.enums.ShipSystem.SENSORS;
import static com.walkertribe.ian.enums.ShipSystem.TORPEDOES;
import static com.walkertribe.ian.enums.ShipSystem.WARP_JUMP_DRIVE;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;

import com.brindyblitz.artemis.engconsole.EngineeringConsoleManager;
import com.brindyblitz.artemis.engconsole.config.InputMapping;
import com.brindyblitz.artemis.engconsole.ui.damcon.Damcon;
import com.brindyblitz.artemis.utils.AudioManager;

import com.walkertribe.ian.enums.OrdnanceType;
import com.walkertribe.ian.enums.ShipSystem;
import com.walkertribe.ian.util.GridCoord;

public class InGamePanel extends TransparentJPanel {

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
		this.add(new CoolantRemainingSlider(last_slider.getWidth(), last_slider.getHeight(), engineeringConsoleManager, audioManager)).setLocation(
				this.numSliders * SLIDER_OFFSET_MULTIPLIER + SLIDER_OFFSET_ADDITIONAL, MAIN_SLIDER_Y);

		PresetPanel presetPanel = new PresetPanel(this.presetManager);
		this.add(presetPanel).setLocation(this.getWidth() - presetPanel.getWidth(), PRESET_Y);

		this.damcon = new Damcon(engineeringConsoleManager, this.audioManager);
		this.add(damcon.getCanvas()).setLocation(10, DAMCON_Y);

		EnergySlider energy_slider = new EnergySlider(engineeringConsoleManager);
		this.add(energy_slider).setLocation(400, 50);

		ForeShieldSlider fore_shield_slider = new ForeShieldSlider(engineeringConsoleManager);
		this.add(fore_shield_slider).setLocation(400, 80);

		AftShieldSlider aft_shield_slider = new AftShieldSlider(engineeringConsoleManager);
		this.add(aft_shield_slider).setLocation(400, 110);
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
			//
			//			for (EnhancedDamconStatus damconStatus : this.engineeringConsoleManager.getDamconTeams().get()) {
			//				System.out.println(damconStatus);
			//			}

			System.out.println("Energy remaining: " + this.engineeringConsoleManager.getTotalEnergyRemaining().get());
			System.out.println("Front shields: " + this.engineeringConsoleManager.getFrontShieldStrength().get());
			System.out.println("Rear shields: " + this.engineeringConsoleManager.getRearShieldStrength().get());
			System.out.println("Front shields max: " + this.engineeringConsoleManager.getFrontShieldMaxStrength().get());
			System.out.println("Rear shields max: " + this.engineeringConsoleManager.getRearShieldMaxStrength().get());

			Map<OrdnanceType, Integer> ordnanceCount = this.engineeringConsoleManager.getOrdnanceCount().get();
			for (Entry<OrdnanceType, Integer> entry : ordnanceCount.entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}

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
	
	/**
	 * Must be called before removing the panel from its parent.
	 * 
	 * Something about the JOGL canvas causes an exception inside Swing if
	 * you don't do this.
	 */
	public void destroy() {
		this.removeAll();
		
	}
}
