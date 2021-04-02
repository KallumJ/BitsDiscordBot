package main;

import commands.Commands;
import events.MessageListener;
import events.ReactionListener;
import main.chatbot.ChatBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

/**
 * A class to implement a discord bot using the Java Discord API
 */
public class Bot {

    private final Commands commands;
    private final Logger logger;
    private final ChatBot chatBot;

    /**
     * Constructs the Bot object, creating a Commands and ChatBot object, and starts a logger.
     */
    public Bot() {
        this.commands = new Commands();
        this.logger = LoggerFactory.getLogger(Bot.class);
        this.chatBot = new ChatBot();
    }

    /**
     * A method to returns the logger for use elsewhere in the program
     *
     * @return The Logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * A method to return the ChatBot object
     *
     * @return The ChatBot
     */
    public ChatBot getChatBot() {
        return chatBot;
    }

    /**
     * A method to initialise the bot by reading the token and starting the JDA instance with all supplied event listeners.
     *
     * @return JDA, JDA object created by starting the bot
     */
    public JDA initBot() {

        try {
            String token = Main.getProperties().getProperty("botToken");

            return JDABuilder.createDefault(token)
                    .addEventListeners(new MessageListener())
                    .addEventListeners(new ReactionListener())
                    .build();
        } catch (LoginException ex) {
            throw new RuntimeException("Error while logging in to Discord", ex);
        }
    }

    /**
     * A method to return the Commands object associated with the Bot.
     *
     * @return Commands, The Commands object.
     */
    public Commands getCommands() {
        return commands;
    }

}
