package events;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import util.json.GamesJSON;

import java.util.Objects;

/**
 * A class to listen to reaction related events
 */
public class ReactionListener extends ListenerAdapter {

    /**
     * A method to be called when a reaction is added to a message
     *
     * @param event the MessageReactionAddEvent that called this method
     */
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        String messageId = event.getMessageId();

        GamesJSON gamesJSON = new GamesJSON(event.getGuild());

        // If the reaction was on the the role set embed
        if (gamesJSON.doesGameJSONIdMatch(messageId)) {
            String role;

            if (gamesJSON.checkEmoteHasRole(event)) {
                role = event.getReaction().getReactionEmote().getEmote().getName();
                Member member = event.getMember();

                gamesJSON.addRole(member, role);
            } else {
                event.getReaction().removeReaction(Objects.requireNonNull(event.getUser())).queue();
            }
        }
    }

    /**
     * A method to be called when a reaction is removed from a message
     *
     * @param event the MessageReactionRemoveEvent that called this method
     */
    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        String messageId = event.getMessageId();

        GamesJSON gamesJSON = new GamesJSON(event.getGuild());

        // If the reaction was on the the role set embed
        if (gamesJSON.doesGameJSONIdMatch(messageId)) {
            String role;

            if (gamesJSON.checkEmoteHasRole(event)) {
                role = event.getReaction().getReactionEmote().getEmote().getName();

                String member = event.getUserId();

                gamesJSON.removeRole(member, role);
            }
        }

    }

}