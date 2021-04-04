package commands.schedule;

import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import schedule.Event;
import schedule.EventsDatabaseConnector;

/**
 * A class to model a command to rsvp to specific events
 */
public class RsvpCommand extends Command {

    private static final String RSVP_SUCCESS_STRING = "Sucessfully RSVPed you for %s!";
    private static final String RSVP_FAILURE_STRING = "I was unable to RSVP you for %s. Did you use the correct format?\n`!bob rsvp <event_name>`";

    /**
     * Constructs an RsvpCommand object
     */
    public RsvpCommand() {
        super("rsvp");
    }

    /**
     * A method to rsvp the user for the specified event
     *
     * @param input The input that executed this command
     * @param event The event that executed this command
     */
    @Override
    public void execute(String input, MessageReceivedEvent event) {
        String eventString = input.replace("rsvp ", "");

        EventsDatabaseConnector databaseConnector = new EventsDatabaseConnector();

        Event eventObj = databaseConnector.getEventFromName(eventString);

        if (databaseConnector.rsvpUser(event.getMember(), eventObj)) {
            event.getChannel().sendMessage(String.format(RSVP_SUCCESS_STRING, eventObj.getName())).queue();
        } else {
            event.getChannel().sendMessage(String.format(RSVP_FAILURE_STRING, eventString)).queue();
        }

    }
}
