package commands.schedule;

import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import schedule.Event;
import schedule.EventsDatabaseConnector;

import java.awt.*;
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
        List<Event> agenda = EventsDatabaseConnector.getAgenda();

        event.getChannel().sendMessage(generateAgendaEmbed(agenda).build()).queue();
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
        embedBuilder.setDescription("These are all the upcoming events on the Bits server!\nDate: dd/mm/yyyy, Timezone: UTC.");
        embedBuilder.setFooter("Have an idea for an event, or think an event is missing? Talk to @KallumJ!");
        embedBuilder.setAuthor("Want to see the agenda, adjusted for your timezone? Click me!", "https://bits.team/events");
        embedBuilder.setColor(new Color(37, 171, 227));

        if (!agenda.isEmpty()) {
            for (Event event : agenda) {
                // If date is before this moment in time
                String timeString = SIMPLE_DATE_FORMAT.format(event.getDate()) + " " + event.getTime().toString().substring(0, 5);
                embedBuilder.addField(event.getName(), timeString, false);
            }
        } else {
            embedBuilder.addField("There are no upcoming events right now.", "", true);
        }

        return embedBuilder;
    }
}
