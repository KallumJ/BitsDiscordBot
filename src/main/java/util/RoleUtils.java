package util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class RoleUtils {

    public static Role createRole(String name, Guild guild) {
        String formattedName = TextUtils.capitaliseEachWord(name);

        List<Role> roles = guild.getRoles();

        for (Role role : roles) {
            if (formattedName.equalsIgnoreCase(role.getName())) {
                throw new RuntimeException("The role " + formattedName + " already exists!");
            }
        }

        return guild.createRole()
                .setName(TextUtils.capitaliseEachWord(formattedName))
                .setMentionable(true)
                .complete();

    }

    public static void addRoleToMember(Role role, Member member, Guild guild) {
        guild.addRoleToMember(member, role).queue();
    }

    public static void removeRoleFromMember(Role role, Member member, Guild guild) {
        guild.removeRoleFromMember(member.getId(), role).queue();
    }
}
