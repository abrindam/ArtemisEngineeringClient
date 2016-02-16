package com.brindyblitz.artemis.utils;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;

public abstract class AudioManager {
    private static HashMap<String, File> soundBank = new HashMap<>();

    public static void initialize(String path) {
        File sfx = new File(path);
        // TODO: SFX > make this recursive so I can better organize assets
        for (File f : sfx.listFiles()) {
            soundBank.put(f.getName(), f);
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
