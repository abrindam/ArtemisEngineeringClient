package com.brindyblitz.artemis.engconsole.config;

import com.walkertribe.ian.enums.ShipSystem;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

public class InputMapping {
    public ShipSystem system;
    public int increaseKey, decreaseKey;
    public String increaseKeyStr, decreaseKeyStr;

    public InputMapping(String inputType, String keyDefintion) {
        String[] split = keyDefintion.split(" ");

        try {
            this.system = ShipSystem.valueOf(inputType.toUpperCase());
            this.increaseKey = keyStringToKeyCode(split[0]);
            this.increaseKeyStr = split[0].toUpperCase();
            this.decreaseKey = keyStringToKeyCode(split[1]);
            this.decreaseKeyStr = split[1].toUpperCase();
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Unable to process key binding line: '" + inputType + "=" + keyDefintion + "'. The format is <system>=<increase key> <decrease key> e.g. 'BEAMS W F3'.");
        }
    }

    public InputMapping(ShipSystem ship_system, int increase_key, int decrease_key) {
        this.system = ship_system;
        this.increaseKey = increase_key;
        this.decreaseKey = decrease_key;
        this.increaseKeyStr = "" + (char)increaseKey;
        this.decreaseKeyStr = "" + (char)decreaseKey;
    }

    private int keyStringToKeyCode(String k)  {
        try {
            Field field = KeyEvent.class.getField("VK_" + k.toUpperCase());
            return field.getInt(null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("No such key: '" + k.toUpperCase() + "'. Make sure the key is listed as VK_<KEY> under https://docs.oracle.com/javase/8/docs/api/java/awt/event/KeyEvent.html. " +
                    "For example, the key '(' is present on that list as 'VK_LEFT_PARENTHESIS' and should be specified in the configuration file as 'LEFT_PARENTHESIS' (without the quotes).");
        } catch (IllegalAccessException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e.getMessage());
        }
    }
}
