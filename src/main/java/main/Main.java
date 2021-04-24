package main;

import net.dv8tion.jda.api.JDA;
import schedule.EventsDatabaseConnector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * The main class to start up the bot
 */
public class Main {

    //TODO: add to aiml files
    //TODO: help command

    private static final String PROPERTIES_FILE_NAME = "config.properties";
    private static final Properties PROPERTIES = new Properties();
    private static Bot BOT;
    private static List<String> arguments;

    /**
     * The main method to start running the bot
     *
     * @param args Given command line arguments, unused.
     */
    public static void main(String[] args) {
        initProperties();
        arguments = Arrays.asList(args);
        BOT = new Bot();
        JDA jda = BOT.initBot();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            jda.shutdown();
            EventsDatabaseConnector.closeConnection();
        }));
    }

    /**
     * A method to initalise the main Properties object before the bot is loaded
     */
    private static void initProperties() {
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            PROPERTIES.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load the properties file", ex);
        }
    }

    /**
     * A method to return the bot object for interaction elsewhere in the program
     *
     * @return Bot, returns the bot object
     */
    public static Bot getBOT() {
        return BOT;
    }

    /**
     * A method to return the Properties object for the Bot
     *
     * @return Properties, the Properties object
     */
    public static Properties getProperties() {
        return PROPERTIES;
    }


    public static List<String> getArguments() {
        return arguments;
    }
}
