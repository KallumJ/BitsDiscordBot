package commands.schedule;

import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import schedule.Event;
import schedule.EventsDatabaseConnector;
import util.PermissionsUtils;

import java.util.Objects;

/**
 * A class to model a command to allow a user to unschedule the provided event
 */
public class UnscheduleCommand extends Command {

    private static final String UNSCHEDULE_SUCCESS_STRING = "Successfully unscheduled event %s";
    private static final String UNSCHEDULE_FAILURE_STRING = "Failed to unschedule event %s. Did you use the correct format?\n`!bob unschedule <event_name>`";

    /**
     * Constructs an UnscheduleCommand object
     */
    public UnscheduleCommand() {
        super("unschedule");
    }

    /**
     * Unschedules the provided event
     *
     * @param input The input that executed this command
     * @param event The event that executed this command
     */
    @Override
    public void execute(String input, MessageReceivedEvent event) {
        if (PermissionsUtils.checkIsGuard(Objects.requireNonNull(event.getMember()))) {

            String eventString = input.replace("unschedule ", "");

            Event eventObj = EventsDatabaseConnector.getEventFromName(eventString);

            if (EventsDatabaseConnector.unscheduleEvent(eventObj)) {
                event.getChannel().sendMessage(String.format(UNSCHEDULE_SUCCESS_STRING, eventObj.getName())).queue();
            } else {
                event.getChannel().sendMessage(String.format(UNSCHEDULE_FAILURE_STRING, eventString)).queue();
            }
        }

    }
}
