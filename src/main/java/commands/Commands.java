package commands;

import commands.music.PauseMusic;
import commands.music.PlayMusic;
import commands.other.PingPong;
import main.Bot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Commands {
    private static final Map<String, Command> commands = new HashMap<>();

    public Commands() {
        addCommand(new PlayMusic());
        addCommand(new PauseMusic());
        addCommand(new PingPong());
    }

    private static void addCommand(@NotNull Command command) {
        commands.put(command.getPhrase(), command);
    }

    public void evaluateCommand(String input, MessageReceivedEvent event) {
        String[] words = input.split(" ");

        for (String word : words) {
            if (commands.containsKey(word)) {

                Command command = commands.get(word);
                command.execute(event);
            } else {
                Bot.didNotUnderstand(event);
            }
        }


    }
}
