package commands.schedule;

import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import schedule.Event;
import schedule.EventsDatabaseConnector;
import schedule.User;

import java.awt.*;
import java.util.List;

/**
 * A class to model a command to list attendees for a specific event
 */
public class AttendeesCommand extends Command {

    /**
     * Constructs the AttendeesCommand object
     */
    public AttendeesCommand() {
        super("attendees");
    }

    /**
     * A method to send the attendees embed
     *
     * @param input The input that executed this command
     * @param event The event that executed this command
     */
    @Override
    public void execute(String input, MessageReceivedEvent event) {
        String eventString = input.replace("attendees ", "");

        Event eventObj = EventsDatabaseConnector.getEventFromName(eventString);
        List<User> attendees = EventsDatabaseConnector.getAttendeesForEvent(eventObj);

        if (!attendees.isEmpty()) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Attendees for " + eventObj.getName());
            embedBuilder.setDescription("These are all the people confirmed to be attending " + eventObj.getName());
            embedBuilder.setFooter("Have an idea for an event, or think an event is missing? Talk to @KallumJ!");
            embedBuilder.setColor(new Color(37, 171, 227));

            for (User attendee : attendees) {
                embedBuilder.addField(attendee.getUsername(), "", true);
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        } else {
            event.getChannel().sendMessage("There are no attendees for this event").queue();
        }
    }
}
