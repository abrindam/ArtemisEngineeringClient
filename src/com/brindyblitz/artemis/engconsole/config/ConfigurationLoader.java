package com.brindyblitz.artemis.engconsole.config;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import net.dhleong.acl.enums.ShipSystem;

public class ConfigurationLoader {

	private static final String CONFIGURATION_FILE_PATH = new File(System.getProperty("user.dir"), "input.cfg").getPath();
	private static final String PRESET_CONFIGURATION_FILE_PATH = new File(System.getProperty("user.dir"), "preset.cfg").getPath();

    private static final int[] RESERVED_KEYS = new int[] {
            KeyEvent.VK_BACK_SLASH,
			KeyEvent.VK_BACK_QUOTE,
            KeyEvent.VK_SPACE,
            KeyEvent.VK_ENTER,
            KeyEvent.VK_0,
            KeyEvent.VK_1,
            KeyEvent.VK_2,
            KeyEvent.VK_3,
            KeyEvent.VK_4,
            KeyEvent.VK_5,
            KeyEvent.VK_6,
            KeyEvent.VK_7,
            KeyEvent.VK_8,
            KeyEvent.VK_9,
    };
	
    public Map<ShipSystem, InputMapping> getInputConfiguration() {
    	
    	Map<ShipSystem, InputMapping> mappings = new HashMap<>();
    	Properties properties = loadConfiguration(CONFIGURATION_FILE_PATH);
        
    	for (Entry<Object, Object> entry : properties.entrySet()) {
    		InputMapping m = new InputMapping((String)entry.getKey(), (String)entry.getValue());
            if (mappings.containsKey(m.system)) {
                throw new RuntimeException("Duplicate key mapping detected for system '" + m.system + "'.");
            }

            for (int reserved : RESERVED_KEYS) {
                if (m.increaseKey == reserved) {
                    throw new RuntimeException("Key mapping using reserved key (" + m.increaseKeyStr + ") detected for system " + m.system);
                } else if (m.decreaseKey == reserved) {
                    throw new RuntimeException("Key mapping using reserved key (" + m.decreaseKeyStr + ") detected for system " + m.system);
                }
            }

			for (InputMapping existing : mappings.values()) {
                if (m.increaseKey == existing.increaseKey || m.increaseKey == existing.decreaseKey) {
                    throw new RuntimeException("Duplicate key mapping detected (" + m.increaseKeyStr + ")");
                } else if (m.decreaseKey == existing.increaseKey || m.decreaseKey == existing.decreaseKey) {
                    throw new RuntimeException("Duplicate key mapping detected (" + m.decreaseKeyStr + ")");
                }
            }

            mappings.put(m.system, m);
		}
        
        return mappings;
    }
    
    public EnergyCoolantPreset[] getPresetConfiguration(int energyDefault, int coolantDefault) {
    	EnergyCoolantPreset[] presets = new EnergyCoolantPreset[10];
    	
    	Properties properties = loadConfiguration(PRESET_CONFIGURATION_FILE_PATH);
    	
    	for (Entry<Object, Object> entry : properties.entrySet()) {
    		try {
	    		String key = (String) entry.getKey();
	    		String[] parts = key.split("_", 3);
	    		int presetNumber = Integer.parseInt(parts[0]);
	    		
	    		if (presets[presetNumber] == null) {
	    			presets[presetNumber] = new EnergyCoolantPreset(energyDefault, coolantDefault);
	    		}
	    		
	    		if (parts[1].equalsIgnoreCase("ENERGY")) {
	    			int value =  ((String) entry.getValue()).equalsIgnoreCase("SAFE") ? EnergyCoolantPreset.SAFE_ENERGY : Integer.parseInt((String) entry.getValue());
	    			presets[presetNumber].setEnergyPreset(ShipSystem.valueOf(parts[2].toUpperCase()), value);
	    		}
	    		else if (parts[1].equalsIgnoreCase("COOLANT")) {
	    			presets[presetNumber].setCoolantPreset(ShipSystem.valueOf(parts[2].toUpperCase()), Integer.parseInt((String) entry.getValue()));
	    		}
	    		else if (parts[1].equalsIgnoreCase("NAME")) {
	    			presets[presetNumber].setName((String) entry.getValue());
	    		}
	    		else {
	    			throw new RuntimeException("Invalid key while parsing line: " + entry.getKey() + "=" + entry.getValue());
	    		}
    		}
	    	catch (NumberFormatException e) {
	    		throw new RuntimeException("Expected integer while parsing line: " + entry.getKey() + "=" + entry.getValue());
			}
    		catch (ArrayIndexOutOfBoundsException e) {
	    		throw new RuntimeException("Invalid preset number (must be 0-9) while parsing line: " + entry.getKey() + "=" + entry.getValue());
			}
    		catch (IllegalArgumentException e) {
    			if (e.getMessage().contains(ShipSystem.class.getName())) {
    				throw new RuntimeException("Invalid ship system while parsing line: " + entry.getKey() + "=" + entry.getValue()); 
    			}
    			else {
    				throw e;    				
    			}
    		}
    	}
    	
    	return presets;
    }
        
    private Properties loadConfiguration(String file) {
    	Properties properties = new Properties();
    	
        try (InputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read configuration file at " + file);
        }
        
        return properties;
    }

}
