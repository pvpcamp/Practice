package camp.pvp.practice.parties;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.PlayerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class Party {

    private Practice plugin;
    private Map<UUID, PartyMember> members;
    private Game game;
    private boolean chooseKits, open;
    public Party(Practice plugin) {
        this.plugin = plugin;
        this.members = new HashMap<>();
        this.chooseKits = false;
        this.open = false;
    }

    public PartyMember join(Player player) {
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        PartyMember member = new PartyMember(player.getUniqueId(), player.getName());
        if(getLeader() == null) {
            member.setLeader(true);
        }

        this.members.put(member.getUuid(), member);

        profile.setParty(this);
        profile.givePlayerItems();

        this.announce("&3[Party] &f" + player.getName() + " &3has joined the party.");

        return member;
    }

    public void leave(Player player) {
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        PartyMember member = members.get(player.getUniqueId());

        profile.setParty(null);
        if(profile.getState().equals(GameProfile.State.LOBBY)) {
            profile.givePlayerItems();
        }

        this.announce("&3[Party] &f" + player.getName() + " &3has left the party.");

        this.members.remove(member.getUuid());

        if(member.isLeader() && members.size() > 0) {
            List<PartyMember> members = new ArrayList<>(getMembers().values());
            Collections.shuffle(members);

            member = members.get(0);
            setLeader(member);
            member.getPlayer().sendMessage(ChatColor.GREEN + "You have randomly been promoted to party leader.");
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
        if(getLeader() != null) {
            final PartyMember leader = getLeader();
            leader.setLeader(false);
            leader.getPlayer().sendMessage(ChatColor.GREEN + "You are no longer party leader.");
            PlayerUtils.giveInteractableItems(leader.getPlayer());
        }

        member.setLeader(true);
        member.getPlayer().sendMessage(ChatColor.GREEN + "You are the new party leader.");
        PlayerUtils.giveInteractableItems(member.getPlayer());
    }

    public void announce(String message) {
        for(PartyMember member : this.getMembers().values()) {
            member.getPlayer().sendMessage(Colors.get(message));
        }
    }
}
