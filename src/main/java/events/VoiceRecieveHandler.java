package events;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import org.jetbrains.annotations.NotNull;
import voice.VoiceProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VoiceRecieveHandler implements AudioReceiveHandler {
    private static final double VOLUME = 1.0;
    private final List<byte[]> recievedBytes = new ArrayList<>();
    private boolean receiving = false;
    private boolean processing = false;

    // How long to wait to see if the user has stopped talking in ms
    private int detectionBuffer = 1000;

    // How often to check whether the user has stopped talking in ms
    private final static int DETECTION_INTERVAL = 100;
    private Thread timer;

    // In milliseconds
    private int howLongTalking;
    private final static int MAXIMUM_TALK_TIME = 5000;
    /**
     * Returns the received bytes of the current utterance
     * @return List of byte arrays
     */
    public List<byte[]> getRecievedBytes() {
        return recievedBytes;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    /**
     * Returns whether we can handle user audio
     * @return True, we can handle user audio
     */
    @Override
    public boolean canReceiveUser() {
        return true;
    }

    /**
     * Saves the users audio, and starts the talking detection
     * @param userAudio The users audio information
     */
    @Override
    public void handleUserAudio(@NotNull UserAudio userAudio) {
        if (!processing) {

            // Reset the detection buffer
            detectionBuffer = 1000;

            // Add the audio information to the list
            try {
                recievedBytes.add(userAudio.getAudioData(VOLUME));
            } catch (OutOfMemoryError ex) {
                VoiceProcessing.leave();
            }

            // Start talking detection
            if (!receiving) {
                receiving = true;
                startTalkingDetection();
            }
        }
    }

    /**
     * Starts the automatic talking detection
     */
    private void startTalkingDetection() {

        // Start checking every 100ms whether the user has stopped talking
        timer = new Thread(() -> {
            while (detectionBuffer > 0) {
                try {
                    // Wait for the provided amount of time
                    TimeUnit.MILLISECONDS.sleep(DETECTION_INTERVAL);

                    // Remove that amount of time from the buffer
                    detectionBuffer -= DETECTION_INTERVAL;

                    // Add that amount of time to the total talk length
                    howLongTalking += DETECTION_INTERVAL;


                    // If the user has reached maximum talk allowance, exit the loop
                    if (howLongTalking == MAXIMUM_TALK_TIME) {
                        detectionBuffer = 0;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Detection buffer has run out or user has exceeded talk time, process the audio buffer
            howLongTalking = 0;
            processAudioBuffer();
        });

        // Start the detection runnable
        timer.start();
    }

    /**
     * Handles the behaviour once the user has stopped talking
     */
    private void processAudioBuffer() {
        // Clear the runnable
        timer = null;

        // Change flag to no longer receiving audio
        receiving = false;

        // Process the audio that has been saved
        processing = true;
        VoiceProcessing.process(recievedBytes);
    }

    public void endProcessing() {
        recievedBytes.clear();
        processing = false;
    }
}
