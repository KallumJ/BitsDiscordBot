package audio.voice;

import audio.SoundManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import java.io.IOException;

/**
 * A class to synthesise speech based on input text
 */
public class SpeechSynthesiser {

    private static final String OUTPUT_FILE = "sounds/output.wav";
    private final SoundManager soundManager;


    /**
     * Constructs a SpeechSynthesiser object
     */
    public SpeechSynthesiser() {
        this.soundManager = new SoundManager();
    }

    /**
     * Produces speech and stores the output wav file in the sounds directory
     *
     * @param text The text to synthesise
     */
    public void synthesiseSpeech(String text) {
        try {
            // Execute text-to-speech script for pico2wave
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "./tts.sh", text);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();

            // Play the generated response back
            playResponseToUser();
        } catch (InterruptedException | IOException ex) {
            throw new RuntimeException("Failed to synthesise speech", ex);
        }
    }

    public void playResponseToUser() {
        this.soundManager.loadSpeechSound(OUTPUT_FILE);
    }

    /**
     * Returns LavaPlayer audio player
     *
     * @return AudioPlayer object used by SpeechSynthesiser
     */
    public AudioPlayer getAudioPlayer() {
        return this.soundManager.getAudioPlayer();
    }
}
