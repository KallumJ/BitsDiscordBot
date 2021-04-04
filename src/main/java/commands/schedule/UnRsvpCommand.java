package commands.schedule;

import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import schedule.Event;
import schedule.EventsDatabaseConnector;

/**
 * A class to model a command to allow users to unrsvp from specific events
 */
public class UnRsvpCommand extends Command {

    private static final String UNRSVP_SUCCESS_STRING = "Succesfully unrsvped you from %s";
    private static final String UNRSVP_FAILURE_STRING = "I was unable to unRSVP you for %s. Did you use the correct format?\n`!bob unrsvp <event_name>`";

    /**
     * Constructs an UnRsvpCommand object
     */
    public UnRsvpCommand() {
        super("unrsvp");
    }

    /**
     * Unrsvps the calling user from the provided event
     *
     * @param input The input that executed this command
     * @param event The event that executed this command
     */
    @Override
    public void execute(String input, MessageReceivedEvent event) {
        String eventString = input.replace("unrsvp ", "");

        EventsDatabaseConnector databaseConnector = new EventsDatabaseConnector();

        Event eventObj = databaseConnector.getEventFromName(eventString);

        if (databaseConnector.unRsvpUser(event.getMember(), eventObj)) {
            event.getChannel().sendMessage(String.format(UNRSVP_SUCCESS_STRING, eventObj.getName())).queue();
        } else {
            event.getChannel().sendMessage(String.format(UNRSVP_FAILURE_STRING, eventString)).queue();
        }
    }
}
