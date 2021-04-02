package main;

import net.dv8tion.jda.api.JDA;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The main class to start up the bot
 */
public class Main {

    //TODO: add to aiml files

    private static final String PROPERTIES_FILE_NAME = "config.properties";
    private static final Properties PROPERTIES = new Properties();
    private static Bot BOT;

    /**
     * The main method to start running the bot
     *
     * @param args Given command line arguments, unused.
     */
    public static void main(String[] args) {

        initProperties();
        BOT = new Bot();
        JDA jda = BOT.initBot();
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

}
