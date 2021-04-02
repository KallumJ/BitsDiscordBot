package commands.audio.voice;

import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import audio.voice.VoiceProcessing;

public class JoinVoice extends Command {

    /**
     * Constructs a JoinVoice object
     */
    public JoinVoice() {
        super("join");
    }

    @Override
    public void execute(String input, MessageReceivedEvent event) {
        VoiceProcessing.join(event);
    }
}
