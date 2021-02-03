package commands;

import commands.bitsplus.WikiLookup;
import commands.other.PingPong;
import main.Main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to register any provided class's that implement the Command abstract class for the bot to use
 */
public class Commands {
    private static final Map<String, Command> commands = new HashMap<>();

    /**
     * Constructs a Commands object. Use addCommand() to add every Command that is available to the bot
     */
    public Commands() {
        addCommand(new PingPong());
        addCommand(new WikiLookup());
    }

    /**
     * A method to add a command to the Map of commands
     *
     * @param command The command to add
     */
    private static void addCommand(@NotNull Command command) {
        commands.put(command.getPhrase(), command);
    }

    /**
     * A method to work out the command the user intends to execute
     *
     * @param input The input from the user
     * @param event The relevant event
     */
    public void evaluateCommand(String input, MessageReceivedEvent event) {
        boolean understood = false;
        String[] words = input.split(" ");

        /* Check every word in the message for a match,
        and execute the relevant command if one is found */
        for (String word : words) {
            if (commands.containsKey(word)) {
                understood = true;
                Command command = commands.get(word);
                command.execute(input, event);
                break;
            }
        }

        // If no command is found, assume the user is chatting
        if (!understood) {
            String response = Main.getBOT().getChatBot().askBot(input);
            event.getChannel().sendMessage(response).queue();
        }


    }
}
