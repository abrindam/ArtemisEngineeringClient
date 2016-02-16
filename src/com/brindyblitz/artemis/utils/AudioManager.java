package com.brindyblitz.artemis.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;

public abstract class AudioManager {
    private static HashMap<String, File> soundBank = new HashMap<>();

    public static void initialize(String path) {
        loadAssetsInDirectory(path, "");
    }

    private static void loadAssetsInDirectory(String path, String prefix) {
        for (File f : new File(path).listFiles()) {
            if (f.isDirectory()) {
                loadAssetsInDirectory(f.getPath(), new File(prefix, f.getName()).getPath());
            } else {
                soundBank.put(new File(prefix, f.getName()).getPath().substring(1), f);
            }
        }
    }

    public static void playSound(String name) {
        try
        {
            File sound = soundBank.get(name);
            if (sound == null) {
                throw new RuntimeException("Unable to locate sound effect '" + name + "'");
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(sound);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (Exception e) {  // LineUnavailableException, IOException, UnsupportedAudioFileException
            e.printStackTrace(System.err);
        }
    }
}
