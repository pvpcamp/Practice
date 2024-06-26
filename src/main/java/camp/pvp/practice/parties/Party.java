package camp.pvp.practice.parties;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.GameProfileManager;
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
    private List<PartyGameRequest> partyGameRequests;
    private Game game;
    private int maxMembers;
    private boolean chooseKits, open;
    public Party(Practice plugin) {
        this.plugin = plugin;
        this.members = new HashMap<>();
        this.partyGameRequests = new ArrayList<>();
        this.chooseKits = false;
        this.open = false;
        this.maxMembers = 8;
    }

    public PartyMember join(Player player) {
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(members.size() >= maxMembers) {
            player.sendMessage(ChatColor.RED + "This party is full.");
            return null;
        }

        if(members.isEmpty() && player.hasPermission("practice.party.no_member_cap")) {
            maxMembers = 100;
        }

        PartyMember member = new PartyMember(player.getUniqueId(), player.getName());

        if(getLeader() == null) member.setLeader(true);

        this.members.put(member.getUuid(), member);

        profile.getPartyInvites().clear();
        profile.setParty(this);
        profile.playerUpdate(false);

        this.announce("&3[Party] &f" + player.getName() + " &3has joined the party.");

        return member;
    }

    public void leave(Player player) {
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        PartyMember member = members.get(player.getUniqueId());

        profile.setParty(null);
        if(profile.getState().equals(GameProfile.State.LOBBY)) {
            profile.playerUpdate(false);
        }

        this.announce("&3[Party] &f" + player.getName() + " &3has left the party.");

        this.members.remove(member.getUuid());

        if(!members.isEmpty()) {
            if(!member.isLeader()) return;

            List<PartyMember> members = new ArrayList<>(getMembers().values());
            Collections.shuffle(members);

            member = members.get(0);
            setLeader(member);
            member.getPlayer().sendMessage(ChatColor.GREEN + "You have randomly been promoted to party leader.");
        } else {
            plugin.getPartyManager().getParties().remove(this);
        }
    }

    public void kick(Player player) {
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        PartyMember member = members.get(player.getUniqueId());

        profile.setParty(null);
        if(profile.getState().equals(GameProfile.State.LOBBY)) {
            profile.playerUpdate(false);
        }

        this.announce("&3[Party] &f" + player.getName() + " &3has been kicked from the party.");

        this.members.remove(member.getUuid());
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
        GameProfileManager gpm = Practice.instance.getGameProfileManager();
        if(getLeader() != null) {
            final PartyMember leader = getLeader();
            leader.setLeader(false);
            leader.getPlayer().sendMessage(ChatColor.GREEN + "You are no longer party leader.");

            GameProfile profile = gpm.getLoadedProfiles().get(member.getUuid());
            if(profile.getState().equals(GameProfile.State.LOBBY_PARTY)) {
                profile.givePlayerItems();
            }
        }

        member.setLeader(true);
        member.getPlayer().sendMessage(ChatColor.GREEN + "You are the new party leader.");

        GameProfile profile = gpm.getLoadedProfiles().get(member.getUuid());
        if(profile.getState().equals(GameProfile.State.LOBBY_PARTY)) {
            profile.givePlayerItems();
        }
    }

    public PartyGameRequest getPartyGameRequest(UUID uuid) {
        for(PartyGameRequest pgr : getPartyGameRequests()) {
            if(!pgr.isExpired()) {
                for(UUID u : pgr.getFromParty().getMembers().keySet()) {
                    if(uuid.equals(u)) {
                        return pgr;
                    }
                }
            }
        }

        return null;
    }

    public void announce(String message) {
        for(PartyMember member : this.getMembers().values()) {
            member.getPlayer().sendMessage(Colors.get(message));
        }
    }
}
