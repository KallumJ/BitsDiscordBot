package events;

import commands.Commands;
import main.Main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * A class to listen for MessageRecievedEvents from the bot
 */
public class MessageListener extends ListenerAdapter {

    /**
     * A method to be executed everytime a MessageRecievedEvent is called
     *
     * @param event The event that was called
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().toLowerCase();
        Commands commands = Main.getBOT().getCommands();

        // When message is intended for bob, check it
        if (message.startsWith("!bob")) {
            commands.evaluateCommand(message.replace("!bob ", ""), event);
        }

    }
}
