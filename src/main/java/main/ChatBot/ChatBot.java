package main.ChatBot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;

import java.io.File;

/**
 * A class to implement a project-ab chat bot
 */
public class ChatBot {
    private final Chat chatSession;

    /**
     * Constructs a ChatBot object, loading the found .aiml files from the /bots directory
     */
    public ChatBot() {
        File resourcesDirectory = new File(".");
        Bot chatBot = new Bot("Bob", resourcesDirectory.getPath());

        this.chatSession = new Chat(chatBot);
    }

    /**
     * A method to ask and return a response from the ChatBot
     * @param msg The query for the bot to respond to
     * @return String, The ChatBot's response
     */
    public String askBot(String msg) {
        return chatSession.multisentenceRespond(msg);
    }

}
