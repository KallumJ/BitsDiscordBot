package commands.schedule;

import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import schedule.Event;
import schedule.EventsDatabaseConnector;

import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * A class to create a command to show the user the current event agenda
 */
public class AgendaCommand extends Command {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * Constructs an Agenda command object
     */
    public AgendaCommand() {
        super("agenda");
    }

    /**
     * Method to send the agenda
     *
     * @param input The input that executed this command
     * @param event The event that executed this command
     */
    @Override
    public void execute(String input, MessageReceivedEvent event) {
        EventsDatabaseConnector databaseConnector = new EventsDatabaseConnector();

        List<Event> agenda = databaseConnector.getAgenda();

        event.getChannel().sendMessage(generateAgendaEmbed(agenda).build()).queue();
    }

    /**
     * A method to check whether there is a valid event present in the passed list of events
     *
     * @param events A List of Event objects
     * @return true if there is a valid event present, false otherwise
     */
    private boolean validEventPresent(List<Event> events) {
        if (!events.isEmpty()) {
            for (Event event : events) {
                if (checkEventValid(event)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the passed event is valid
     *
     * @param event the event to check
     * @return true if event is valid, false otherwise
     */
    private boolean checkEventValid(Event event) {
        // If date is before this moment in time
        return event.getDate().compareTo(new Date(System.currentTimeMillis())) > 0;
    }

    /**
     * Generates the agenda embed
     *
     * @param agenda List of event objects to include in the agenda
     * @return EmbedBuilder with relevant information
     */
    private EmbedBuilder generateAgendaEmbed(List<Event> agenda) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Current Agenda");
        embedBuilder.setDescription("These are all the upcoming events on the Bits server!\nDate: dd/mm/yyyy, Time: UK Time.");
        embedBuilder.setFooter("Have an idea for an event, or think an event is missing? Talk to @KallumJ!");
        embedBuilder.setAuthor("Want to see the agenda, adjusted for your timezone? Click me!", "https://bits.team/events");
        embedBuilder.setColor(new Color(37, 171, 227));

        if (validEventPresent(agenda)) {
            for (Event event : agenda) {
                // If date is before this moment in time
                if (checkEventValid(event)) {
                    String timeString = SIMPLE_DATE_FORMAT.format(event.getDate()) + " " + event.getTime().toString().substring(0, 5);
                    embedBuilder.addField(event.getName(), timeString, false);
                }
            }
        } else {
            embedBuilder.addField("There are no upcoming events right now.", "", true);
        }

        return embedBuilder;
    }
}
