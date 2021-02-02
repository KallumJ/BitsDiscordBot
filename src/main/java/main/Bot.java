package main;

import commands.Commands;
import events.MessageListener;
import main.ChatBot.ChatBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A class to implement a discord bot using the Java Discord API
 */
public class Bot {
    public static final String PROPERTIES_FILE_NAME = "config.properties";

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
     * @return The Logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * A method to return the ChatBot object
     * @return The ChatBot
     */
    public ChatBot getChatBot() {
        return chatBot;
    }

    /**
     * A method to initialise the bot.
     * @return JDA, JDA object created by starting the bot
     */
    public JDA initBot() {

        try {
            String token = readToken();

            return JDABuilder.createDefault(token)
                    .addEventListeners(new MessageListener())
                    .build();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read token file", ex);
        } catch (LoginException ex) {
            throw new RuntimeException("Error while logging in to Discord", ex);
        }
    }

    /**
     * A method to return the Commands object associated with the Bot.
     * @return Commands, The Commands object.
     */
    public Commands getCommands() {
        return commands;
    }

    /**
     * A method to read the bot token from a .properties file
     * @return String, the bot token
     * @throws IOException When the token file is missing
     */
    public String readToken() throws IOException {
        // Load properties file into Properties object
        Properties properties = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
        properties.load(inputStream);

        return properties.getProperty("botToken");
    }
}
