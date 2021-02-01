package commands.other;

import commands.Command;
import main.Bot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class PingPong extends Command {

    public PingPong() {
        super("ping");
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Pong!").queue();
    }
}
