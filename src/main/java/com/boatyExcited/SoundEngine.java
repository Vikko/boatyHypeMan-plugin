package com.boatyExcited;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class SoundEngine {
    private static Clip clip = null;
    public static void playSound(Sound sound) {
        try {
            InputStream stream = new BufferedInputStream(SoundFileManager.getSoundStream(sound));
            AudioInputStream is = AudioSystem.getAudioInputStream(stream);
            AudioFormat format = is.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(is);
            setVolume(100);
            clip.start();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            log.warn("Sound file error", e);
        }
    }

    private static void setVolume(int volume) {
        float vol = volume / 100.0f;
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20.0f * (float) Math.log10(vol));
    }
}
