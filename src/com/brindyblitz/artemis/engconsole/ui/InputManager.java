package com.brindyblitz.artemis.engconsole.ui;

import net.dhleong.acl.enums.ShipSystem;

import java.awt.event.KeyEvent;
import java.io.*;
import java.util.HashMap;

public class InputManager {
    private static final String CONFIGURATION_FILE_PATH = new File(System.getProperty("user.dir"), "input.cfg").getPath();
    public HashMap<ShipSystem, InputMapping> mappings = new HashMap<ShipSystem, InputMapping>();

    private static final boolean DBG_PRINT_MAPPINGS = false;

    public InputManager() {
        loadConfigurationFile();

        if (DBG_PRINT_MAPPINGS && mappings.size() > 0) {
            System.out.println("Custom key bindings loaded:");
            for (ShipSystem s : mappings.keySet()) {
                System.out.println("\t" + s + ": +" + mappings.get(s).increaseKeyStr + " / -" + mappings.get(s).decreaseKeyStr);
            }
        }

        fillEmptyMappingsWithDefaults();
    }

    private void loadConfigurationFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(CONFIGURATION_FILE_PATH))) {
            for (String line; (line = br.readLine()) != null; ) {
                InputMapping m = new InputMapping(line);
                if (this.mappings.containsKey(m.system)) {
                    throw new RuntimeException("Duplicate key mapping detected for system '" + m.system + "'.");
                }
                this.mappings.put(m.system, m);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read configuration file at " + CONFIGURATION_FILE_PATH);
        }
    }

    private void fillEmptyMappingsWithDefaults() {
        for (ShipSystem system : ShipSystem.values()) {
            InputMapping mapping = this.mappings.get(system);
            if (mapping == null) {
                switch (system) {
                    case BEAMS:
                        mapping = new InputMapping(system, KeyEvent.VK_Q, KeyEvent.VK_A);
                        break;

                    case TORPEDOES:
                        mapping = new InputMapping(system, KeyEvent.VK_W, KeyEvent.VK_S);
                        break;

                    case SENSORS:
                        mapping = new InputMapping(system, KeyEvent.VK_E, KeyEvent.VK_D);
                        break;

                    case MANEUVERING:
                        mapping = new InputMapping(system, KeyEvent.VK_R, KeyEvent.VK_F);
                        break;

                    case IMPULSE:
                        mapping = new InputMapping(system, KeyEvent.VK_T, KeyEvent.VK_G);
                        break;

                    case WARP_JUMP_DRIVE:
                        mapping = new InputMapping(system, KeyEvent.VK_Y, KeyEvent.VK_H);
                        break;

                    case FORE_SHIELDS:
                        mapping = new InputMapping(system, KeyEvent.VK_U, KeyEvent.VK_J);
                        break;

                    case AFT_SHIELDS:
                        mapping = new InputMapping(system, KeyEvent.VK_I, KeyEvent.VK_K);
                        break;
                }

                this.mappings.put(system, mapping);
            }
        }
    }
}
