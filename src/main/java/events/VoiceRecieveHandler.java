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
    private List<byte[]> recievedBytes = new ArrayList<>();

    private boolean receiving = false;
    private int detectionBuffer = 1000;
    private final static int DETECTION_INTERVAL = 100;
    private Thread timer;

    /**
     * Returns the received bytes of the current utterance
     * @return List of byte arrays
     */
    public List<byte[]> getRecievedBytes() {
        return recievedBytes;
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
            startTalkingDetection();
            receiving = true;
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
                    TimeUnit.MILLISECONDS.sleep(DETECTION_INTERVAL);

                    detectionBuffer -= DETECTION_INTERVAL;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Detection buffer has run out, the user has stopped talking
            userStoppedTalking();
        });

        // Start the detection runnable
        timer.start();
    }

    /**
     * Handles the behaviour once the user has stopped talking
     */
    private void userStoppedTalking() {
        // Clear the runnable
        timer = null;

        // Change flag to no longer receiving audio
        receiving = false;

        // Process the audio that has been saved
        VoiceProcessing.process(recievedBytes);
    }
}
