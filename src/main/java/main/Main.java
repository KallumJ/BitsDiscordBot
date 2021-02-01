package main;

import net.dv8tion.jda.api.JDA;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main {

    public static Bot bot = new Bot();

    public static void main(String[] args) throws IOException, LoginException {
        JDA jda = bot.initBot();
    }
}
