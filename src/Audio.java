package src;

import javax.sound.sampled.*;
import java.io.*;

public class Audio {
    private static Clip musicClip;

    public static void playMainMenuMusic(float volume) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(
                new File("sounds/pac-man-theme-remix.wav")
            );
            musicClip = AudioSystem.getClip();
            musicClip.open(audio);

            FloatControl vol = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
            vol.setValue(volume * 50 / 100 - 50);

            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playPing(float volume) {
        playSound("sounds/sfx_coin_single2.wav", volume);
    }

    public static void playPowerUp(float volume) {
        playSound("sounds/sfx_sounds_powerup6.wav", volume);
    }

    private static void playSound(String filename, float volume) {
        // volume is between 0 and 100
        if (volume == 0)
            return;

        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(filename));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);

            // change volume
            FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            vol.setValue(volume * 50 / 100 - 50);

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
