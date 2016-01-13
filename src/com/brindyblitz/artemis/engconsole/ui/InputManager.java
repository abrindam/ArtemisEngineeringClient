package com.brindyblitz.artemis.engconsole.ui;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import com.brindyblitz.artemis.engconsole.config.ConfigurationLoader;
import com.brindyblitz.artemis.engconsole.config.InputMapping;

import net.dhleong.acl.enums.ShipSystem;

public abstract class InputManager {
    public static Map<ShipSystem, InputMapping> mappings = new HashMap<ShipSystem, InputMapping>();
    public static Map<ShipSystem, InputMapping> defaultMappings = new HashMap<ShipSystem, InputMapping>();

    private static final boolean DBG_PRINT_MAPPINGS = false;

    public static void init() {
        mappings = new ConfigurationLoader().getInputConfiguration();

        if (DBG_PRINT_MAPPINGS && mappings.size() > 0) {
            System.out.println("Custom key bindings loaded:");
            for (ShipSystem s : mappings.keySet()) {
                System.out.println("\t" + s + ": +" + mappings.get(s).increaseKeyStr + " / -" + mappings.get(s).decreaseKeyStr);
            }
        }

        generateDefaultMappings();
        fillEmptyMappingsWithDefaults();
    }

    private static void generateDefaultMappings() {
        for (ShipSystem system : ShipSystem.values()) {
            InputMapping default_mapping;

            switch (system) {
                case BEAMS:
                    default_mapping = new InputMapping(system, KeyEvent.VK_Q, KeyEvent.VK_A);
                    break;

                case TORPEDOES:
                    default_mapping = new InputMapping(system, KeyEvent.VK_W, KeyEvent.VK_S);
                    break;

                case SENSORS:
                    default_mapping = new InputMapping(system, KeyEvent.VK_E, KeyEvent.VK_D);
                    break;

                case MANEUVERING:
                    default_mapping = new InputMapping(system, KeyEvent.VK_R, KeyEvent.VK_F);
                    break;

                case IMPULSE:
                    default_mapping = new InputMapping(system, KeyEvent.VK_T, KeyEvent.VK_G);
                    break;

                case WARP_JUMP_DRIVE:
                    default_mapping = new InputMapping(system, KeyEvent.VK_Y, KeyEvent.VK_H);
                    break;

                case FORE_SHIELDS:
                    default_mapping = new InputMapping(system, KeyEvent.VK_U, KeyEvent.VK_J);
                    break;

                case AFT_SHIELDS:
                    default_mapping = new InputMapping(system, KeyEvent.VK_I, KeyEvent.VK_K);
                    break;

                default:
                    throw new RuntimeException("Invalid ship system: " + system + "!");
            }

            defaultMappings.put(system, default_mapping);
        }
    }

    private static void fillEmptyMappingsWithDefaults() {
        for (ShipSystem system : ShipSystem.values()) {
            InputMapping custom = mappings.get(system);
            InputMapping default_mapping = defaultMappings.get(system);

            for (InputMapping existing : mappings.values()) {
                if (default_mapping.increaseKey == existing.increaseKey || default_mapping.increaseKey == existing.decreaseKey) {
                    if (custom != null) {
                        continue;
                    }

                    throw new RuntimeException("Increase key already bound (" + default_mapping.increaseKeyStr + ") for system " + default_mapping.system);
                } else if (default_mapping.increaseKey == existing.increaseKey || default_mapping.decreaseKey == existing.decreaseKey) {
                    if (custom != null) {
                        continue;
                    }

                    throw new RuntimeException("Decrease key already bound (" + default_mapping.decreaseKeyStr + ") for system " + default_mapping.system);
                }
            }

            if (custom == null) {
                mappings.put(system, default_mapping);
            }
        }
    }
}
