package commands.games;

import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.PermissionsUtils;
import util.TextUtils;
import util.json.GamesJSON;

import java.util.Objects;

public class AddGame extends Command {
    public AddGame() {
        super("addgame");
    }

    @Override
    public void execute(String input, MessageReceivedEvent event) {
        if (PermissionsUtils.checkIsGuard(Objects.requireNonNull(event.getMember()))) {
            String[] arguments = input.replace(super.getPhrase() + " ", "").split(" ", 2);

            String game = arguments[1];
            String reaction = arguments[0];


            GamesJSON gamesJSON = new GamesJSON(event.getGuild());
            gamesJSON.addGame(game, reaction);
            event.getChannel().sendMessage("Added game " + TextUtils.capitaliseEachWord(game) + " to games.json").queue();
        }
    }
}
