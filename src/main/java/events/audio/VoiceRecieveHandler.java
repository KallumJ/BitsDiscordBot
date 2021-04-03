package events.audio;

import audio.voice.VoiceProcessor;
import commands.Commands;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A class to handle the receiving of audio from the user
 */
public class VoiceRecieveHandler implements AudioReceiveHandler {
    private static final double VOLUME = 1.0;
    private final static int DETECTION_INTERVAL = 100;  // How often to check whether the user has stopped talking in ms
    private final static int MAXIMUM_TALK_TIME = 5000; // In milliseconds
    private final List<byte[]> recievedBytes = new ArrayList<>();
    private final VoiceProcessor voiceProcessor = Commands.getJoinVoiceCommand().getVoiceProcessor();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private boolean receiving = false;
    private boolean processing = false;
    private int detectionBuffer = 1000; // How long to wait to see if the user has stopped talking in ms
    private int howLongTalking; // In milliseconds

    /**
     * Returns the received bytes of the current utterance
     *
     * @return List of byte arrays
     */
    public List<byte[]> getRecievedBytes() {
        return recievedBytes;
    }

    /**
     * Returns whether the bot is currently processing audio information
     *
     * @return true if processing, false otherwise
     */
    public boolean isProcessing() {
        return processing;
    }

    /**
     * Sets whether the bot is processing or not.
     *
     * @param processing boolean, whether the bot is processing, true if it is, false otherwise
     */
    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    /**
     * Returns whether we can handle user audio
     *
     * @return True, we can handle user audio
     */
    @Override
    public boolean canReceiveUser() {
        return true;
    }

    /**
     * Saves the users audio, and starts the talking detection
     *
     * @param userAudio The users audio information
     */
    @Override
    public void handleUserAudio(@NotNull UserAudio userAudio) {
        // If we are currently processing information, ignore incoming information
        if (!processing) {

            // Reset the detection buffer
            detectionBuffer = 1000;

            // Add the audio information to the list
            try {
                recievedBytes.add(userAudio.getAudioData(VOLUME));
            } catch (OutOfMemoryError ex) {
                voiceProcessor.leave();
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
        this.executorService.submit(() -> {
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
                } catch (InterruptedException ex) {
                    break;
                }
            }

            // Detection buffer has run out or user has exceeded talk time, process the audio buffer
            if (detectionBuffer == 0) {
                howLongTalking = 0;
                processAudioBuffer();
            }
        });

    }

    /**
     * Handles the behaviour once the user has stopped talking
     */
    private void processAudioBuffer() {

        // Change flag to no longer receiving audio
        receiving = false;

        // Process the audio that has been saved
        processing = true;
        voiceProcessor.process(recievedBytes);
    }

    /**
     * Method to be called whenever processing of audio has ended.
     */
    public void endProcessing() {
        // Clear the buffer of information
        recievedBytes.clear();

        // Indicate processing is over.
        processing = false;
    }
}
