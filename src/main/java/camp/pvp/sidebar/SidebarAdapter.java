package camp.pvp.sidebar;

import camp.pvp.Practice;
import camp.pvp.arenas.Arena;
import camp.pvp.games.Game;
import camp.pvp.games.GameManager;
import camp.pvp.games.GameTeam;
import camp.pvp.kits.HCFKit;
import camp.pvp.parties.Party;
import camp.pvp.parties.PartyMember;
import camp.pvp.profiles.GameProfile;
import camp.pvp.profiles.GameProfileManager;
import camp.pvp.queue.GameQueue;
import camp.pvp.queue.GameQueueManager;
import camp.pvp.queue.GameQueueMember;
import camp.pvp.utils.TimeUtil;
import io.github.thatkawaiisam.assemble.AssembleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SidebarAdapter implements AssembleAdapter {

    private Practice plugin;
    private GameManager gameManager;
    private GameProfileManager gameProfileManager;
    private GameQueueManager gameQueueManager;
    public SidebarAdapter(Practice plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        this.gameProfileManager = plugin.getGameProfileManager();
        this.gameQueueManager = plugin.getGameQueueManager();
    }

    @Override
    public String getTitle(Player player) {
        return "&6&lPvP Camp &7[Beta]";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        GameProfile profile = gameProfileManager.getLoadedProfiles().get(player.getUniqueId());
        if(profile != null) {
            GameProfile.State state = profile.getState();

            lines.add("&7&m------------------");
            switch(state) {
                case LOBBY:
                    int online = Bukkit.getOnlinePlayers().size();
                    lines.add("&6Online: &f" + online);
                    lines.add("&6In Game: &f" + gameManager.getTotalInGame());
                    if(profile.isDebugMode()) {
                        lines.add(" ");
                        lines.add("&6Debug:");
                        lines.add(" &6&oBuild mode: &f" + profile.isBuildMode());
                        lines.add(" &6&oLC Players: &f" + plugin.getLunarClientAPI().getPlayersRunningLunarClient().size() + "/" + online);
                    }
                    break;
                case LOBBY_QUEUE:
                    GameQueue queue = gameQueueManager.getQueue(player);
                    GameQueueMember queueMember = gameQueueManager.findQueueMember(queue, player.getUniqueId());
                    boolean ranked = queue.getType().equals(GameQueue.Type.UNRANKED);
                    lines.add("&6Online: &f" + Bukkit.getOnlinePlayers().size());
                    lines.add("&6In Game: &f" + gameManager.getTotalInGame());
                    lines.add(" ");
                    lines.add("&6In Queue:");
                    lines.add(" &7● " + queue.getDuelKit().getColor() + queue.getDuelKit().getDisplayName() + (ranked ? " &f(U)" : "&f&l(R)"));
                    lines.add(" &7● &f" + TimeUtil.get(new Date(), queueMember.getJoined()));
//                    if(ranked) {
//                        lines.add(" &7● &f(900-1100)");
//                    }
                    break;
                case LOBBY_PARTY:
                    lines.add("&6Online: &f" + Bukkit.getOnlinePlayers().size());
                    lines.add("&6In Game: &f" + gameManager.getTotalInGame());
                    lines.add(" ");
                    Party party = profile.getParty();
                    PartyMember member = party.getMembers().get(player.getUniqueId());
                    HCFKit kit = member.getHcfKit();
                    GameTeam.Color color = member.getTeamColor();
                    lines.add("&6Party &7(" + party.getMembers().size() + ")");
                    lines.add("&6Leader: &f" + party.getLeader().getName());
                    lines.add("&6HCF Kit: " + kit.getColor() + kit.toString());
                    lines.add("&6Assigned Team: " + color.getChatColor() + color.getName());

                    if(party.getGame() != null) {
                        lines.add("&7&oParty In Game");
                    }
                    break;
                case IN_GAME:
                    Game game = profile.getGame();
                    lines.addAll(game.getScoreboard(profile));
                    break;
                case SPECTATING:
                    game = profile.getGame();
                    lines.addAll(game.getSpectatorScoreboard(profile));
                    break;
                case KIT_EDITOR:
                    lines.add("&6Online: &f" + Bukkit.getOnlinePlayers().size());
                    lines.add("&6In Game: &f" + gameManager.getTotalInGame());
                    lines.add(" ");
                    lines.add("&6Editing Kit:");
                    lines.add(" &f" + profile.getEditingKit().getDisplayName());
                    lines.add(" ");
                    lines.add("&6Sign: &fLeave");
                    lines.add("&6Chest: &fMore Items");
                    lines.add("&6Anvil: &fSave");
                    break;
                default:
                    lines.add("&f&oIn Development.");
            }
            lines.add(" ");
            lines.add("&6pvp.camp");
            lines.add("&7&m------------------");
        }

        return lines;
    }
}
