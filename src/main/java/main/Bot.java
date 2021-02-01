package main;

import commands.Commands;
import events.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Bot {
    public static final String PROPERTIES_FILE_NAME = "config.properties";

    public Commands commands;

    public Bot() {
        this.commands = new Commands();
    }

    public static void sendMessage(String message) {
        MessageChannel channel = MessageListener.getLastMessageEvent().getChannel();

        channel.sendMessage(message).queue();
    }

    public static void didNotUnderstand() {
        sendMessage("I'm sorry... I did not understand!");
    }

    public Commands getCommands() {
        return commands;
    }

    // A method to initialise and start the bot
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

    public String readToken() throws IOException {
        Properties properties = new Properties();

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
        properties.load(inputStream);

        return properties.getProperty("botToken");
    }
}
