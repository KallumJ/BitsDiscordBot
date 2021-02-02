package main;

import net.dv8tion.jda.api.JDA;

/**
 * The main class to start up the bot
 */
public class Main {

    public static final Bot BOT = new Bot();

    /**
     * The main method to start running the bot
     * @param args Given command line arguments, unused.
     */
    public static void main(String[] args) {
        JDA jda = BOT.initBot();
    }

    /**
     * A method to return the bot object for interaction elsewhere in the program
     * @return Bot, returns the bot object
     */
    public static Bot getBOT() {
        return BOT;
    }
}
