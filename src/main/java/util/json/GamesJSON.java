package util.json;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/**
 * A class to model the games json file used by the bot
 */
public class GamesJSON {
    private static final String GAMES_FILE_NAME = "games.json";
    private final JSONObject gamesJson;
    private final Guild guild;

    /**
     * A constructor for the GamesJSON class, that reads the games json file and loads it into a JSONObject
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
     * A method to create a JSON String from the games file
     *
     * @return String, The String of JSON data from file
     */
    private String parseGamesFileToString() {
        return JSON.findJsonStringFromFile(new File(Objects.requireNonNull(this.getClass().getClassLoader()
                .getResource(GAMES_FILE_NAME)).getFile()));
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
    public void addRole(Member member, String emote) {
        Role role = findRoleFromName(emote);
        this.guild.addRoleToMember(member, role).queue();
    }

    /**
     * A method to remove the role associated with the provided emote from the provided member
     *
     * @param memberId String, the user id of the member
     * @param emote    String, the name of the emote
     */
    public void removeRole(String memberId, String emote) {
        Role role = findRoleFromName(emote);
        this.guild.removeRoleFromMember(memberId, role).queue();
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
     * A method to return a Map with a name key and emote value of all the games in the games json file
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
     * A method to update the current message id stored in the games json file
     *
     * @param messageId String, the message id to update to
     */
    public void updateMessageID(String messageId) {
        try {
            this.gamesJson.put("messageId", messageId);


            // Rewrite JSON object to file
            FileWriter fileWriter = new FileWriter(new File(Objects.requireNonNull(this.getClass().getClassLoader()
                    .getResource(GAMES_FILE_NAME)).getFile()));
            fileWriter.write(this.gamesJson.toString());
            fileWriter.flush();
            fileWriter.close();

        } catch (JSONException | IOException ex) {
            throw new RuntimeException("Unable to read message id from gamesJson");
        }
    }

    /**
     * A method to check whether the emote associated with the provided event is associated with a role
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
            throw new RuntimeException("Failed to find element in games json file");
        }

        return false;
    }

    /**
     * A method to return the games array in the games json file
     * @return JSONArray, the found JSONArray
     */
    private JSONArray getGamesJsonArray() {
        try {
            return gamesJson.getJSONArray("games");
        } catch (JSONException ex) {
            throw new RuntimeException("Failed to load the games array from games.json");
        }
    }
}
