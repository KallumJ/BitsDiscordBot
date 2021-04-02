package commands.audio.voice;

import audio.voice.VoiceProcessor;
import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class JoinVoice extends Command {

    private final VoiceProcessor voiceProcessor;

    /**
     * Constructs a JoinVoice object
     */
    public JoinVoice() {
        super("join");
        this.voiceProcessor = new VoiceProcessor();
    }

    @Override
    public void execute(String input, MessageReceivedEvent event) {
        // Join the voice channel and begin listening
        voiceProcessor.invoke(event);
    }


    /**
     * Returns the VoiceProcessor associated with this session
     *
     * @return the VoiceProcessor
     */
    public VoiceProcessor getVoiceProcessor() {
        return voiceProcessor;
    }
}
