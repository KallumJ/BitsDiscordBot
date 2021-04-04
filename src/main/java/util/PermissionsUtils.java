package util;

import main.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

/**
 * A class to assist in the checking of user permissions for the bot
 */
public class PermissionsUtils {

    private static final String GUARD_ROLE_ID = Main.getProperties().getProperty("guardRoleId");

    /**
     * Checks whether the provided user is a guard
     *
     * @param user the user to check
     * @return true if the user is a guard, false otherwise
     */
    public static boolean checkIsGuard(Member user) {
        for (Role role : user.getRoles()) {
            if (role.getId().equals(GUARD_ROLE_ID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check the bot has permissions to join the voice channel
     *
     * @param guild The guild
     * @return true if the bot can join, false otherwise.
     */
    public static boolean checkBotCanJoinVoice(Guild guild) {
        return guild.getSelfMember().hasPermission(Permission.VOICE_CONNECT);
    }
}
