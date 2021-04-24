package commands.games;

import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.TextUtils;
import util.json.GamesJSON;

//TODO: remove game command

public class AddGame extends Command {
    public AddGame() {
        super("addgame");
    }

    @Override
    public void execute(String input, MessageReceivedEvent event) {
        String[] arguments = input.replace(super.getPhrase() + " ", "").split(" ", 2);

        String game = arguments[1];
        String reaction = arguments[0];


        GamesJSON gamesJSON = new GamesJSON(event.getGuild());
        gamesJSON.addGame(game, reaction);
        event.getChannel().sendMessage("Added game " + TextUtils.capitaliseEachWord(game) + " to games.json").queue();
    }


}
