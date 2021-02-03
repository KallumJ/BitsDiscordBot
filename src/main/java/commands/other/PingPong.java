package commands.other;

import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PingPong extends Command {
    public PingPong() {
        super("ping");
    }

    @Override
    public void execute(String input, MessageReceivedEvent event) {
        event.getChannel().sendMessage("Pong!").queue();
    }
}
