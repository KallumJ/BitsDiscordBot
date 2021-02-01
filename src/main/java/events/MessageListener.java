package events;

import commands.Commands;
import main.Main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageListener extends ListenerAdapter {
    public static MessageReceivedEvent lastMessageEvent;

    public static MessageReceivedEvent getLastMessageEvent() {
        return lastMessageEvent;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        lastMessageEvent = event;

        String message = event.getMessage().getContentRaw().toLowerCase();
        Commands commands = Main.bot.getCommands();

        // When message is intended for bob
        if (message.startsWith("!bob")) {
            commands.evaluateCommand(message.replace("!bob ", ""));
        }

    }
}
