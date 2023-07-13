package camp.pvp.parties;

import camp.pvp.Practice;
import camp.pvp.profiles.GameProfile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class Party {

    private Practice plugin;
    private Map<UUID, PartyMember> members;
    public Party(Practice plugin) {
        this.plugin = plugin;
        this.members = new HashMap<>();
    }

    public PartyMember join(Player player) {
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        PartyMember member = new PartyMember(player.getUniqueId(), player.getName());
        if(getLeader() == null) {
            member.setLeader(true);
        }

        profile.setParty(this);
        profile.givePlayerItems();

        this.members.put(member.getUuid(), member);
        return member;
    }

    public void leave(Player player) {
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        PartyMember member = members.get(player.getUniqueId());

        profile.setParty(null);
        if(profile.getState().equals(GameProfile.State.LOBBY)) {
            profile.givePlayerItems();
        }

        this.members.remove(member.getUuid());

        if(members.size() > 0) {
            if (getLeader() == null) {
                List<PartyMember> members = new ArrayList<>(getMembers().values());
                Collections.shuffle(members);

                member = members.get(0);
                member.setLeader(true);
                member.getPlayer().sendMessage(ChatColor.GREEN + "You have randomly been promoted to party leader.");
            }
        } else {
            plugin.getPartyManager().getParties().remove(this);
        }
    }

    public PartyMember getLeader() {
        for(PartyMember member : members.values()) {
            if(member.isLeader()) {
                return member;
            }
        }

        return null;
    }

    public void setLeader(PartyMember member) {
        getLeader().setLeader(false);
        getLeader().getPlayer().sendMessage(ChatColor.GREEN + "You are no longer party leader.");

        member.setLeader(true);
        member.getPlayer().sendMessage(ChatColor.GREEN + "You are the new party leader.");
    }
}
