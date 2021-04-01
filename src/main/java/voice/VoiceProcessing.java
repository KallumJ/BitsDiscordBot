package voice;

import events.VoiceRecieveHandler;
import main.Main;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;
import java.util.Objects;

public class VoiceProcessing {

    private static final String NO_VOICE_CHANNEL = "You are not currently connected to a voice channel";
    private static AudioManager audioManager;
    private static VoiceRecieveHandler voiceRecieveHandler;
    private static MessageReceivedEvent messageReceivedEvent;

    /**
     * Process the provided audio information
     * @param bytes The audio information as a byte array
     */
    public static void process(List<byte[]> bytes) {
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
        if (result.startsWith("bob")) {
            String query = result.replace("bob", "");

            messageReceivedEvent.getChannel().sendMessage("Bob thinks you said: " + query).queue();
            Main.getBOT().getCommands().evaluateCommand(query, messageReceivedEvent);
        }

        // Clear the stored audio information, ready for next query
        voiceRecieveHandler.getRecievedBytes().clear();
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

        if (voiceChannel != null) {
            audioManager = voiceChannel.getGuild().getAudioManager();
            audioManager.openAudioConnection(voiceChannel);

            voiceRecieveHandler = new VoiceRecieveHandler();
            audioManager.setReceivingHandler(voiceRecieveHandler);
        } else {
            event.getChannel().sendMessage(NO_VOICE_CHANNEL).queue();
        }
    }

}
