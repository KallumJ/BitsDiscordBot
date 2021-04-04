package commands;

import commands.audio.voice.JoinVoice;
import commands.audio.voice.LeaveVoice;
import commands.bitsplus.WikiLookup;
import commands.other.GamesRoleMessage;
import commands.schedule.*;
import main.Main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A class to register any provided class's that implement the Command abstract class for the bot to use
 */
public class Commands {
    private static final Map<String, Command> commandsMap = new HashMap<>();

    /**
     * Constructs a Commands object. Use addCommand() to add every Command that is available to the bot
     */
    public Commands() {
        addCommand(new WikiLookup());
        addCommand(new GamesRoleMessage());
        addCommand(new JoinVoice());
        addCommand(new LeaveVoice());
        addCommand(new AgendaCommand());
        addCommand(new ScheduleCommand());
        addCommand(new RsvpCommand());
        addCommand(new UnRsvpCommand());
        addCommand(new AttendeesCommand());
        addCommand(new UnscheduleCommand());
    }

    /**
     * A method to add a command to the Map of commands
     *
     * @param command The command to add
     */
    private static void addCommand(@NotNull Command command) {
        commandsMap.put(command.getPhrase(), command);
    }

    /**
     * Return the current JoinVoice command
     *
     * @return the current JoinVoice command
     */
    public static JoinVoice getJoinVoiceCommand() {
        return (JoinVoice) commandsMap.get("join");
    }

    /**
     * A method to work out the command the user intends to execute
     *
     * @param input The input from the user
     * @param event The relevant event
     */
    public Optional<String> evaluateCommand(String input, MessageReceivedEvent event) {
        //TODO: Allow multiple command phrases
        boolean understood = false;
        String[] words = input.split(" ");

        /* Check every word in the message for a match,
        and execute the relevant command if one is found */
        for (String word : words) {
            if (commandsMap.containsKey(word)) {
                understood = true;
                Command command = commandsMap.get(word);
                command.execute(input, event);
                break;
            }
        }

        // If no command is found, assume the user is chatting
        Optional<String> optResponse = Optional.empty();
        if (!understood) {
            optResponse = Optional.ofNullable(Main.getBOT().getChatBot().askBot(input));
            optResponse.ifPresent(response -> event.getChannel().sendMessage(response).queue());
        }
        return optResponse;
    }
}
