package commands.other;

import commands.Command;
import main.Bot;


public class PingPong extends Command {

    public PingPong() {
        setPhrase("ping");
    }

    @Override
    public void execute() {
        Bot.sendMessage("Pong!");
    }
}
