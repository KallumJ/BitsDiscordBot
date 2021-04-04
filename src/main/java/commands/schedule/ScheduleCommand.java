package commands.schedule;

import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import schedule.Event;
import schedule.EventsDatabaseConnector;
import util.PermissionsUtils;
import util.TextUtils;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

/**
 * A class to model a command to schedule events with the provided information
 */
public class ScheduleCommand extends Command {

    private static final String SCHEDULE_FAILURE_STRING = "I was unable to schedule this event... Did you enter the information in the correct format?\n`!bob schedule <event_date (yyyy-MM-dd)> <event_time (hh:mm:ss)> <event_name>`";
    private static final String SCHEDULE_SUCCESS_STRING = "%s was successfully scheduled for %s at %s";

    /**
     * Constructs a ScheduleCommand object
     */
    public ScheduleCommand() {
        super("schedule");
    }

    /**
     * A method to schedule the event
     *
     * @param input The input that executed this command
     * @param event The event that executed this command
     */
    @Override
    public void execute(String input, MessageReceivedEvent event) {
        if (PermissionsUtils.checkIsGuard(Objects.requireNonNull(event.getMember()))) {
            // Get event information from message as an array
            String[] eventInfoArray = input.replace("schedule ", "").split(" ", 3);

            // If there are the correct number of arguments
            if (eventInfoArray.length == 3) {
                Event eventObj = new Event(TextUtils.capitaliseEachWord(eventInfoArray[2]), Date.valueOf(eventInfoArray[0]), Time.valueOf(eventInfoArray[1]));

                EventsDatabaseConnector databaseConnector = new EventsDatabaseConnector();
                if (databaseConnector.scheduleEvent(eventObj)) {
                    event.getChannel().sendMessage(String.format(SCHEDULE_SUCCESS_STRING,
                            eventObj.getName(), eventObj.getDate().toString(), eventObj.getTime().toString())).queue();
                } else {
                    sendScheduleFailMessage(event);
                }
            } else {
                sendScheduleFailMessage(event);
            }
        }
    }

    /**
     * Sends error message to user
     *
     * @param event the calling MessageReceivedEvent
     */
    private void sendScheduleFailMessage(MessageReceivedEvent event) {
        event.getChannel().sendMessage(SCHEDULE_FAILURE_STRING).queue();
    }
}
