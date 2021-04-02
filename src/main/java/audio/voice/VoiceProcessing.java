package audio.voice;

import events.audio.AudioPlayerSendHandler;
import events.audio.VoiceRecieveHandler;
import main.Main;
import net.dv8tion.jda.api.Permission;
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

public class VoiceProcessing {

    private static final String NO_VOICE_CHANNEL = "I can not connect to a voice channel right now.";
    private static final String HAIL_WORDS_FILE = "hailwords.txt";
    private static AudioManager audioManager;
    private static VoiceRecieveHandler voiceRecieveHandler;
    private static MessageReceivedEvent messageReceivedEvent;

    private static SpeechSynthesiser speechSynthesiser;

    /**
     * Process the provided audio information
     *
     * @param bytes The audio information as a byte array
     */
    public static void process(List<byte[]> bytes) {
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
            String query = getQuery(result);
            messageReceivedEvent.getChannel().sendMessage("Bob thinks you said: " + query).queue();
            Optional<String> response = Main.getBOT().getCommands().evaluateCommand(query, messageReceivedEvent);

            response.ifPresent(string -> speechSynthesiser.synthesiseSpeech(string));
        }

        // Flag that processing has ended
        voiceRecieveHandler.endProcessing();
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
        for (String hailWord : getHailedWords()) {
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
    private static ArrayList<String> getHailedWords() {
        ArrayList<String> hailWords = new ArrayList<>();
        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(HAIL_WORDS_FILE);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader);

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
    public static void leave() {
        voiceRecieveHandler = null;

        if (audioManager != null) {
            audioManager.closeAudioConnection();
        }
    }

    /**
     * Bot joins the channel the member who sent the message is currently in
     *
     * @param event The event that hailed bob
     */
    public static void join(MessageReceivedEvent event) {
        messageReceivedEvent = event;
        VoiceChannel voiceChannel = Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel();

        if (voiceChannel != null && event.getGuild().getSelfMember().hasPermission(Permission.VOICE_CONNECT)) {
            audioManager = voiceChannel.getGuild().getAudioManager();

            voiceRecieveHandler = new VoiceRecieveHandler();
            audioManager.setReceivingHandler(voiceRecieveHandler);

            speechSynthesiser = new SpeechSynthesiser();
            audioManager.setSendingHandler(new AudioPlayerSendHandler(speechSynthesiser.getAudioPlayer()));

            audioManager.openAudioConnection(voiceChannel);
        } else {
            event.getChannel().sendMessage(NO_VOICE_CHANNEL).queue();
        }
    }

}
