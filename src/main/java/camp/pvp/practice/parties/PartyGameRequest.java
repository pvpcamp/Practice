package camp.pvp.practice.parties;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.impl.teams.HCFTeams;
import camp.pvp.practice.games.impl.teams.TeamDuel;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.utils.Colors;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class PartyGameRequest {

    public enum Type {
        TEAMS, HCF
    }

    private final Party fromParty, toParty;
    private final Type type;
    private DuelKit kit;
    private Date expires;

    public PartyGameRequest(Party fromParty, Party toParty, Type type) {
        this.fromParty = fromParty;
        this.toParty = toParty;
        this.type = type;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 30);

        this.expires = calendar.getTime();
    }

    public void send() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 30);

        this.expires = calendar.getTime();

        fromParty.getLeader().getPlayer().sendMessage(ChatColor.GREEN + "You sent a duel request to " + ChatColor.WHITE + toParty.getLeader().getName() + "'s Party" + ChatColor.GREEN + ".");

        StringBuilder sb = new StringBuilder();
        sb.append("\n&6&lNew Party Duel Request\n");
        sb.append("\n &7● &6From: &f" + fromParty.getLeader().getName());

        if(type.equals(Type.HCF)) {
            sb.append("\n &7● &6Game Type: &fHCF Team Fight");
        } else {
            sb.append("\n &7● &6Game Type: &fParty vs Party");
            sb.append("\n &7● &6Kit: &f" + kit.getDisplayName());
        }
        sb.append("\n");
        TextComponent msg = new TextComponent(Colors.get("&6[Click to accept this duel]"));

        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept " + fromParty.getLeader().getName()));
        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Colors.get("&a/accept " + fromParty.getLeader().getName())).create()));

        Player toPlayer = toParty.getLeader().getPlayer();
        toPlayer.sendMessage(Colors.get(sb.toString()));
        toPlayer.spigot().sendMessage(msg);
        toPlayer.sendMessage(" ");

        toParty.getPartyGameRequests().add(this);
    }

    public boolean isExpired() {
        return this.expires.before(new Date());
    }

    public boolean startGame() {
        if(toParty.getGame() == null && fromParty.getGame() == null) {
            if(type.equals(Type.TEAMS)) {
                TeamDuel teamDuel = new TeamDuel(Practice.instance, UUID.randomUUID());
                teamDuel.setKit(kit);

                for (PartyMember member : toParty.getMembers().values()) {
                    GameParticipant p = teamDuel.join(member.getPlayer());
                    p.setTeam(teamDuel.getBlue());
                }

                for (PartyMember member : fromParty.getMembers().values()) {
                    GameParticipant p = teamDuel.join(member.getPlayer());
                    p.setTeam(teamDuel.getRed());
                }

                List<Party> parties = teamDuel.getParties();
                parties.add(toParty);
                parties.add(fromParty);

                toParty.getPartyGameRequests().clear();
                fromParty.getPartyGameRequests().clear();

                teamDuel.initialize();
            } else {
                HCFTeams teamDuel = new HCFTeams(Practice.instance, UUID.randomUUID());

                for (PartyMember member : toParty.getMembers().values()) {
                    GameParticipant p = teamDuel.join(member.getPlayer());
                    p.setAppliedHcfKit(member.getHcfKit());
                    p.setTeam(teamDuel.getBlue());
                }

                for (PartyMember member : fromParty.getMembers().values()) {
                    GameParticipant p = teamDuel.join(member.getPlayer());
                    p.setAppliedHcfKit(member.getHcfKit());
                    p.setTeam(teamDuel.getRed());
                }

                List<Party> parties = teamDuel.getParties();
                parties.add(toParty);
                parties.add(fromParty);

                toParty.getPartyGameRequests().clear();
                fromParty.getPartyGameRequests().clear();

                teamDuel.initialize();
            }

            return true;
        } else {
            return false;
        }
    }
}
