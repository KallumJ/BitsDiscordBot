package commands.audio.voice;

import audio.voice.VoiceProcessor;
import commands.Command;
import commands.Commands;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A class to allow bob to leave a voice channel
 */
public class LeaveVoice extends Command {

    /**
     * Constructs a LeaveVoice object
     */
    public LeaveVoice() {
        super("leave");
    }

    @Override
    public void execute(String input, MessageReceivedEvent event) {
        // Leave the current voice channel
        VoiceProcessor voiceProcessor = Commands.getJoinVoiceCommand().getVoiceProcessor();
        voiceProcessor.leave();
    }
}
