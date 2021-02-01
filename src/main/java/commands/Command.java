package commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class Command {
    private final String phrase;

    public Command(String phrase) {
        this.phrase = phrase;
    }

    public String getPhrase() {
        return phrase;
    }

    public abstract void execute(MessageReceivedEvent event);
}
