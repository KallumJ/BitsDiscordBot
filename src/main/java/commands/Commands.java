package commands;

import commands.music.PauseMusic;
import commands.music.PlayMusic;
import commands.other.PingPong;

import java.util.ArrayList;

public class Commands {
    private static final ArrayList<Command> commands = new ArrayList<>();

    public Commands() {
        commands.add(new PlayMusic());
        commands.add(new PauseMusic());
        commands.add(new PingPong());
    }

    public void evaluateCommand(String input) {
        String[] words = input.split(" ");
        ArrayList<String> phrases = new ArrayList<>();

        for (Command command : commands) {
            phrases.add(command.getPhrase());
        }

        for (String word : words) {
            if (phrases.contains(word)) {

                // Execute command of the found phrase
                int index = phrases.indexOf(word);

                Command command = commands.get(index);
                command.execute();
            }
        }


    }
}
