package commands.audio.voice;

import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import audio.voice.VoiceProcessing;

public class LeaveVoice extends Command {

    public LeaveVoice() {
        super("leave");
    }

    @Override
    public void execute(String input, MessageReceivedEvent event) {
        VoiceProcessing.leave();
    }
}
