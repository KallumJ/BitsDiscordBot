package main.ChatBot;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;

import java.io.File;

/**
 * A class to implement a project-ab chat bot
 */
public class ChatBot {
    private static final String NO_UNDERSTAND_MSG = "Oops... I don't understand... I'm sorry! #BlameKall";
    private final Chat chatSession;

    //TODO: handle <oob>s

    /**
     * Constructs a ChatBot object, loading the found .aiml files from the /bots directory, and deleting it's previous learnings
     */
    public ChatBot() {
        File learnedFile = new File("bots/Bob/aimlif/learnf.aiml.csv");
        if (learnedFile.exists()) {
            learnedFile.delete();
        }

        File resourcesDirectory = new File(".");
        Bot chatBot = new Bot("Bob", resourcesDirectory.getPath());

        this.chatSession = new Chat(chatBot);
    }

    /**
     * A method to ask and return a response from the ChatBot
     *
     * @param msg The query for the bot to respond to
     * @return String, The ChatBot's response
     */
    public String askBot(String msg) {
        try {
            return chatSession.multisentenceRespond(msg);
        } catch (NoClassDefFoundError ex) {
            return NO_UNDERSTAND_MSG;
        }
    }

}
