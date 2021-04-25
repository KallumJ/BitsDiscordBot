package commands.games;

import commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.PermissionsUtils;
import util.TextUtils;
import util.json.GamesJSON;

import java.util.Objects;

public class RemoveGame extends Command {

    public RemoveGame() {
        super("removegame");
    }

    @Override
    public void execute(String input, MessageReceivedEvent event) {
        if (PermissionsUtils.checkIsGuard(Objects.requireNonNull(event.getMember()))) {

            String game = TextUtils.capitaliseEachWord(input.replace( super.getPhrase() + " ", ""));

            GamesJSON gamesJSON = new GamesJSON(event.getGuild());
            gamesJSON.removeGame(game);

            event.getChannel().sendMessage(game + " was removed from games.json").queue();
        }
    }
}
