package commands.other;

import commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.PermissionsUtils;
import util.json.GamesJSON;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

/**
 * A command to update the games role message currently on the server
 */
public class GamesRoleMessage extends Command {
    private GamesJSON gamesJSON;

    /**
     * Constructs a Command object
     */
    public GamesRoleMessage() {
        super("gamerole");
        this.gamesJSON = null;
    }

    /**
     * A method to update the role set embed
     *
     * @param input The input that executed this command
     * @param event The event that executed this command
     */
    @Override
    public void execute(String input, MessageReceivedEvent event) {
        if (PermissionsUtils.checkIsGuard(Objects.requireNonNull(event.getMember()))) {

            this.gamesJSON = new GamesJSON(event.getGuild());

            // If user wants a new role set embed message
            if (input.contains("new")) {
                removeOldRoleSetEmbed(event);

                event.getChannel().sendMessage(generateNewRoleSetEmbed(event).build()).queue(message -> {
                    // Update the message id stored in games file
                    String messageId = message.getId();
                    gamesJSON.updateMessageID(messageId);
                });
            } else {
                // Else, edit the one thats already there
                event.getChannel().retrieveMessageById(gamesJSON.findMessageID()).queue(message -> {
                    message.editMessage(generateNewRoleSetEmbed(event).build()).queue();
                });
            }

            event.getMessage().delete().queue();
        }
    }

    /**
     * A method to remove the previous role set embed
     *
     * @param event The event that executed this command
     */
    private void removeOldRoleSetEmbed(MessageReceivedEvent event) {
        String currentMessageID = gamesJSON.findMessageID();
        event.getChannel().retrieveMessageById(currentMessageID).queue(message -> {
            message.delete().queue();
        });
    }

    /**
     * A method to generate the new role set embed
     *
     * @param event The event that executed this command
     * @return EmbedBuilder, the EmbedBuilder containing the information for the role set message
     */
    private EmbedBuilder generateNewRoleSetEmbed(MessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Game Roles");
        embedBuilder.setDescription("Have a game you like? Wanna be notified when someone is looking to play? React with the relevant emote to be assigned the role!");
        embedBuilder.setFooter("Have a favourite game that doesnt appear on this list? Ask @KallumJ!");
        embedBuilder.setColor(new Color(37, 171, 227));

        HashMap<String, String> gamesMap = gamesJSON.findAllGames();

        gamesMap.forEach((game, emote) -> {
            embedBuilder.addField(game, emote, false);
        });

        return embedBuilder;
    }
}
