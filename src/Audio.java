package src;

import javax.sound.sampled.*;
import java.io.*;

public class Audio {
    private static float volume;

    private static Clip musicClip, pingClip, powerUpClip, damageClip, ghostKillClip;
    private static FloatControl musicVolumeControl, pingVolumeControl, powerUpVolumeControl, damageVolumeControl, ghostKillVolumeControl;

    public static void initialize() {
        musicClip = readAudioFile("sounds/pac-man-theme-remix.wav");
        pingClip = readAudioFile("sounds/sfx_coin_single2.wav");
        powerUpClip = readAudioFile("sounds/sfx_sounds_powerup6.wav");
        damageClip = readAudioFile("sounds/sfx_sounds_damage3.wav");
        ghostKillClip = readAudioFile("sounds/sfx_sounds_powerup4.wav");

        musicVolumeControl = (FloatControl) musicClip.getControl(FloatControl.Type.MASTER_GAIN);
        pingVolumeControl = (FloatControl) pingClip.getControl(FloatControl.Type.MASTER_GAIN);
        powerUpVolumeControl = (FloatControl) powerUpClip.getControl(FloatControl.Type.MASTER_GAIN);
        damageVolumeControl = (FloatControl) damageClip.getControl(FloatControl.Type.MASTER_GAIN);
        ghostKillVolumeControl = (FloatControl) ghostKillClip.getControl(FloatControl.Type.MASTER_GAIN);

        changeVolume(20);
    }

    private static Clip readAudioFile(String filename) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(
                new File(filename)
            );
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // getter for volume
    public static float getVolume() {
        return volume;
    }

    public static void changeVolume(float newVolume) {
        volume = newVolume;
        float db = newVolume * 50 / 100 - 50;
        if (newVolume == 0)
            db = -80;
        musicVolumeControl.setValue(db);
        pingVolumeControl.setValue(db);
        powerUpVolumeControl.setValue(db);
        damageVolumeControl.setValue(db);
        ghostKillVolumeControl.setValue(db);
    }

    public static void playMainMenuMusic() {
        // Description: This method plays the main menu music and loops it continuously.
        // Parameters: None
        // Return: void

        if (musicClip.isRunning())
            return;
        musicClip.setFramePosition(0);
        musicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public static void stopMainMenuMusic() {
        // Description: This method plays the main menu music and loops it continuously.
        // Parameters: None
        // Return: void

        musicClip.stop();
    }

    public static void playPing() {
        // Description: This method plays the ping sound effect.
        // Parameters: None
        // Return: void

        pingClip.setFramePosition(0);
        pingClip.start();
    }

    public static void playPowerUp() {
        // Description: This method plays the power up sound effect.
        // Parameters: None
        // Return: void

        powerUpClip.setFramePosition(0);
        powerUpClip.start();
    }

    public static void playDamage() {
        // Description: This method plays the lose hearts sound effect.
        // Parameters: None
        // Return: void

        damageClip.setFramePosition(0);
        damageClip.start();
    }

    public static void playGhostKill() {
        // Description: This method plays the lose hearts sound effect.
        // Parameters: None
        // Return: void

        ghostKillClip.setFramePosition(0);
        ghostKillClip.start();
    }
}
