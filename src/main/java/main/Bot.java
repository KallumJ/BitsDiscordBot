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
import java.util.Properties;

public class Bot {
    public static final File PROPERTIES_FILE = new File("src/main/resources/config.properties");
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
    public JDA initBot() throws LoginException, IOException {

        String token = readToken();

        return JDABuilder.createDefault(token)
                .addEventListeners(new MessageListener())
                .build();
    }

    public String readToken() throws IOException {
        Properties properties = new Properties();

        FileInputStream fileInputStream = new FileInputStream(PROPERTIES_FILE);
        properties.load(fileInputStream);

        return properties.getProperty("botToken");
    }
}
