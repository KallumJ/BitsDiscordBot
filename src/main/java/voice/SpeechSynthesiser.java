package voice;

import java.io.IOException;

/**
 * A class to synthesise speech based on input text
 */
public class SpeechSynthesiser {

    /**
     * Produces speech and stores the output wav file in the sounds directory
     * @param text The text to synthesise
     */
    public void synthesiseSpeech(String text) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "./tts.sh", text);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
        } catch (InterruptedException | IOException ex) {
            throw new RuntimeException("Failed to synthesise speech", ex);
        }
    }

    public void playResponseToUser() {

    }
}
