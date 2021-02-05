package commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A superclass for all commands
 */
public abstract class Command {
    private final String phrase;

    /**
     * Constructs a Command object
     *
     * @param phrase The phrase required to execute this command
     */
    public Command(String phrase) {
        this.phrase = phrase;
    }

    /**
     * A method to return the phrase required to execute this command
     *
     * @return String, the phrase
     */
    public String getPhrase() {
        return phrase;
    }

    /**
     * A method to be implemented by the developer to implement the command's behaviour
     *
     * @param input The input that executed this command
     * @param event The event that executed this command
     */
    public abstract void execute(String input, MessageReceivedEvent event);
}
