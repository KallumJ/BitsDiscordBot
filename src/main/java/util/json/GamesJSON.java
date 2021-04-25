package util.json;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.RoleUtils;
import util.TextUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * A class to model the commands.games json file used by the bot
 */
public class GamesJSON {
    private static final String GAMES_FILE_PATH = "games.json";
    private final JSONObject gamesJson;
    private final Guild guild;

    /**
     * A constructor for the GamesJSON class, that reads the commands.games json file and loads it into a JSONObject
     *
     * @param guild The server to set roles within
     */
    public GamesJSON(Guild guild) {
        this.guild = guild;

        try {
            this.gamesJson = new JSONObject(parseGamesFileToString());
        } catch (JSONException ex) {
            throw new RuntimeException("Failed to load JSONObject for games file");
        }

    }

    /**
     * A method to create a JSON String from the commands.games file
     *
     * @return String, The String of JSON data from file
     */
    private String parseGamesFileToString() {
        return JSON.findJsonStringFromFile(new File(GAMES_FILE_PATH));
    }

    /**
     * A method to return the message id of the current role set embed on the server
     *
     * @return String, The message id
     */
    public String findMessageID() {
        try {
            return this.gamesJson.getString("messageId");
        } catch (JSONException ex) {
            throw new RuntimeException("Failed to get message id from games file");
        }
    }

    /**
     * A method to determine whether the provided message id matches the current message id of the role set embed on the server
     *
     * @param messageId The message id to check
     * @return boolean, true if they match, false otherwise
     */
    public boolean doesGameJSONIdMatch(String messageId) {
        return findMessageID().equals(messageId);
    }

    /**
     * A method to add the role associated with the provided emote to the provided member
     *
     * @param member Member, the member to add the role too
     * @param emote  String, the name of the emote
     */
    public void assignRoleToMember(Member member, String emote) {
        Role role = findRoleFromName(emote);

        RoleUtils.addRoleToMember(role, member, guild);
    }

    /**
     * A method to remove the role associated with the provided emote from the provided member
     *
     * @param member Member, the member
     * @param emote  String, the name of the emote
     */
    public void unassignRoleFromMember(Member member, String emote) {
        Role role = findRoleFromName(emote);

        RoleUtils.removeRoleFromMember(role, member, guild);
    }

    /**
     * A method to find the Role object of the role associated with the provided emote name
     *
     * @param name String, the emote name
     * @return Role, the found Role object
     */
    public Role findRoleFromName(String name) {
        try {
            JSONArray gamesJSONArray = gamesJson.getJSONArray("games");
            for (int i = 0; i < gamesJSONArray.length(); i++) {
                JSONObject currentJSONObject = gamesJSONArray.getJSONObject(i);
                String currentName = currentJSONObject.getString("reaction");

                if (currentName.equals(name)) {
                    String currentId = currentJSONObject.getString("roleID");
                    return this.guild.getRoleById(currentId);
                }
            }
        } catch (JSONException ex) {
            throw new RuntimeException("Failed to load the games array from games.json");
        }

        throw new RuntimeException("Unable to find role by id");
    }

    /**
     * A method to return a Map with a name key and emote value of all the commands.games in the commands.games json file
     *
     * @return HashMap The generated HashMap
     */
    public HashMap<String, String> findAllGames() {
        HashMap<String, String> hashMap = new HashMap<>();

        try {
            JSONArray gamesJSONArray = getGamesJsonArray();
            for (int i = 0; i < gamesJSONArray.length(); i++) {
                JSONObject currentJSONObject = gamesJSONArray.getJSONObject(i);
                String currentGame = currentJSONObject.getString("name");

                // Add a space before every capitalised character
                currentGame = currentGame.replaceAll("\\d+", "").replaceAll("(.)([A-Z])", "$1 $2");

                String currentReaction = currentJSONObject.getString("reaction");
                String currentReactionId = this.guild.getEmotesByName(currentReaction, true).get(0).getId();

                // Generate the emote string required to display emote in embed
                String emoteString = "<:" + currentReaction + ":" + currentReactionId + ">";

                hashMap.put(currentGame, emoteString);
            }

            return hashMap;
        } catch (JSONException ex) {
            throw new RuntimeException("Failed to load element from games.json");
        }

    }

    /**
     * A method to update the current message id stored in the commands.games json file
     *
     * @param messageId String, the message id to update to
     */
    public void updateMessageID(String messageId) {
        try {
            this.gamesJson.put("messageId", messageId);

            updateGamesFile();
        } catch (JSONException ex) {
            throw new RuntimeException("Unable to read message id from gamesJson");
        }
    }

    private void updateGamesFile() {
        try {
            // Rewrite JSON object to file
            FileWriter fileWriter = new FileWriter(GAMES_FILE_PATH);
            fileWriter.write(this.gamesJson.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to update the games.json file");
        }

    }

    /**
     * A method to check whether the emote associated with the provided event is associated with a role
     *
     * @param event The reaction event
     * @return boolean, true if emote has associated role, false otherwise;
     */
    public boolean checkEmoteHasRole(GenericMessageReactionEvent event) {

        // If sentReaction is not custom, return false
        String sentReaction;
        try {
            sentReaction = event.getReaction().getReactionEmote().getEmote().getName();
        } catch (IllegalStateException ex) {
            return false;
        }

        JSONArray gamesJsonArray = getGamesJsonArray();

        try {
            for (int i = 0; i < gamesJsonArray.length(); i++) {
                JSONObject currentGame = gamesJsonArray.getJSONObject(i);
                String currentGameReaction = currentGame.getString("reaction");


                // If associated role found
                if (currentGameReaction.equals(sentReaction)) {
                    return true;
                }
            }
        } catch (JSONException ex) {
            throw new RuntimeException("Failed to find element in commands.games json file");
        }

        return false;
    }

    /**
     * A method to return the commands.games array in the commands.games json file
     *
     * @return JSONArray, the found JSONArray
     */
    private JSONArray getGamesJsonArray() {
        try {
            return gamesJson.getJSONArray("games");
        } catch (JSONException ex) {
            throw new RuntimeException("Failed to load the commands.games array from commands.games.json");
        }
    }

    public void addGame(String game, String reaction) {
        Role role = RoleUtils.createRole(game, guild);

        try {
            JSONObject gameObj = new JSONObject();
            gameObj.put("name", TextUtils.capitaliseEachWord(game));
            gameObj.put("roleID", role.getId());
            gameObj.put("reaction", reaction);

            JSONArray gamesArray = getGamesJsonArray();
            gamesArray.put(gameObj);

            this.gamesJson.remove("games");
            this.gamesJson.put("games", gamesArray);
            updateGamesFile();
        } catch (JSONException ex) {
            throw new RuntimeException("Unable to add game", ex);
        }

    }

    public void removeGame(String game) {
       JSONArray gamesArray = getGamesJsonArray();
       JSONArray updatedGamesArray = new JSONArray();

       try {
           for (int i = 0; i < gamesArray.length(); i++) {
               JSONObject gameObj = gamesArray.getJSONObject(i);

               // ADd all games that are not the specified game to new array
               if (gameObj.getString("name").equals(game)) {
                   removeReactionFromMessage(gameObj.getString("reaction"));
               } else {
                   updatedGamesArray.put(gameObj);
               }
           }

           this.gamesJson.remove("games");
           this.gamesJson.put("games", updatedGamesArray);
           updateGamesFile();
       } catch (JSONException ex) {
           throw new RuntimeException("Failed remove the game from games.json");
       }
    }

    public void removeReactionFromMessage(String reaction) {
        for (Emote emote : guild.getEmotes()) {
            if (emote.getName().equals(reaction)) {
                TextChannel gameRolesChannel = guild.getTextChannelsByName("game-roles", false).get(0);
                gameRolesChannel.clearReactionsById(findMessageID(), emote).queue();
            }
        }
    }

}
