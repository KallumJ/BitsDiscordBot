package audio.voice;

import events.audio.AudioPlayerSendHandler;
import events.audio.VoiceRecieveHandler;
import main.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class VoiceProcessor {

    private static final String NO_VOICE_CHANNEL = "I can't connect to a voice channel right now.";
    private static final String HAIL_WORDS_FILE = "hailwords.txt";
    private AudioManager audioManager;
    private VoiceRecieveHandler voiceRecieveHandler;
    private VoiceChannel voiceChannel;
    private TextChannel textChannel;
    private MessageReceivedEvent callingEvent;
    private SpeechSynthesiser speechSynthesiser;
    private static final ArrayList<String> HAIL_WORDS = getHailWords();

    /**
     * Method to call when Bob has been invoked. Sets up the requirements to listen to the conversation
     *
     * @param callingEvent The event that invoked Bob
     */
    public void invoke(MessageReceivedEvent callingEvent) {
        this.callingEvent = callingEvent;
        this.voiceChannel = Objects.requireNonNull(
                Objects.requireNonNull(callingEvent.getMember()).getVoiceState())
                .getChannel();
        this.textChannel = callingEvent.getTextChannel();

        this.audioManager = this.voiceChannel.getGuild().getAudioManager();


        // Set event handlers for receiving and sending audio information
        this.voiceRecieveHandler = new VoiceRecieveHandler();
        this.audioManager.setReceivingHandler(voiceRecieveHandler);

        this.speechSynthesiser = new SpeechSynthesiser();
        this.audioManager.setSendingHandler(new AudioPlayerSendHandler(speechSynthesiser.getAudioPlayer()));

        join();
    }

    /**
     * Process the provided audio information
     *
     * @param bytes The audio information as a byte array
     */
    public void process(List<byte[]> bytes) {
        // Check voice receive handler is still active (i.e, Bob hasn't been instructed to leave)
        if (voiceRecieveHandler != null) {
            voiceRecieveHandler.setProcessing(true);

            // Get the number of bytes received
            int size = 0;
            for (byte[] receivedByte : bytes) {
                size += receivedByte.length;
            }

            // Turn the list of arrays into one big array
            byte[] allAudioData = new byte[size];
            int i = 0;
            for (byte[] receivedByte : bytes) {
                for (byte b : receivedByte) {
                    allAudioData[i] = b;
                    i++;
                }
            }

            // Transcribe audio information
            SpeechRecogniser speechRecogniser = new SpeechRecogniser();
            String result = speechRecogniser.transcribeAudioFromByteArray(allAudioData);

            // If message hails bob, evaluate their query
            if (checkIfHailed(result)) {
                // Find what the user said
                String query = getQuery(result);
                System.out.println("Bob thinks you said: " + query);

                // Evaluate what the user said
                Optional<String> response = Main.getBOT().getCommands().evaluateCommand(query, callingEvent);

                // If Bob would reply with words, speak them
                response.ifPresent(speechSynthesiser::synthesiseSpeech);
            }

            // Flag that processing has ended
            voiceRecieveHandler.endProcessing();
        }
    }

    /**
     * Removes the hail word from the passed text
     *
     * @param text The text to remove the hail word from
     * @return The text without the hail word
     */
    private static String getQuery(String text) {
        return text.split(" ", 2)[1];
    }

    /**
     * Checks the passed text is hailing Bob
     *
     * @param text The text to check
     * @return true, if bob is being hailed
     */
    private static boolean checkIfHailed(String text) {
        boolean hailed = false;
        for (String hailWord : HAIL_WORDS) {
            if (text.startsWith(hailWord)) {
                hailed = true;
                break;
            }
        }
        return hailed;
    }

    /**
     * Retrieves the list of common words heard instead of "bob" from resources directory
     *
     * @return The list of words, as a String ArrayList
     */
    private static ArrayList<String> getHailWords() {
        ArrayList<String> hailWords = new ArrayList<>();
        try {
            // Open the hail words file
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(HAIL_WORDS_FILE);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader);

                // Read every word in the hailed words file, and add to list
                String line;
                while ((line = reader.readLine()) != null) {
                    hailWords.add(line);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read the hail words file", ex);
        }

        return hailWords;
    }

    /**
     * Bot leaves the channel its currently in
     */
    public void leave() {
        if (audioManager != null) {
            audioManager.closeAudioConnection();
        }
    }

    /**
     * Bot joins the channel the member who sent the message is currently in
     */
    public void join() {
        if (this.voiceChannel != null && callingEvent.getGuild().getSelfMember().hasPermission(Permission.VOICE_CONNECT)) {
            this.audioManager.openAudioConnection(voiceChannel);
        } else {
            textChannel.sendMessage(NO_VOICE_CHANNEL).queue();
        }
    }
}
