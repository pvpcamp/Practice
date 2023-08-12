package camp.pvp.practice.sidebar;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.*;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameManager;
import camp.pvp.practice.games.tournaments.Tournament;
import camp.pvp.practice.kits.HCFKit;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.parties.PartyMember;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.queue.GameQueueManager;
import camp.pvp.practice.queue.GameQueueMember;
import camp.pvp.practice.utils.TimeUtil;
import io.github.thatkawaiisam.assemble.AssembleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;

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
        return plugin.getConfig().getString("scoreboard.title");
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        GameProfile profile = gameProfileManager.getLoadedProfiles().get(player.getUniqueId());
        if(profile != null && profile.isShowSidebar()) {
            GameProfile.State state = profile.getState();
            boolean showInGame = profile.isSidebarInGame(),
                    showCps = profile.isSidebarShowCps(),
                    showDuration = profile.isSidebarShowDuration(),
                    showLines = profile.isSidebarShowLines(),
                    showPing = profile.isSidebarShowPing(),
                    staff = player.hasPermission("practice.staff");

            if(showLines) {
                lines.add("&7&m------------------");
            } else {
                lines.add(" ");
            }

            switch(state) {
                case LOBBY:
                    int online = Bukkit.getOnlinePlayers().size();
                    int inGame = gameManager.getTotalInGame();
                    int staffOnline = 0;

                    for(Player p : Bukkit.getOnlinePlayers()) {
                        if(p.hasPermission("practice.staff")) {
                            staffOnline++;
                        }
                    }

                    lines.add("&6Online: &f" + online);
                    lines.add("&6In Game: &f" + inGame);

                    if(staff) {
                        lines.add("&6Staff Online: &f" + staffOnline);
                        lines.add("&6Staff Mode: &f" + (profile.isStaffMode() ? "Enabled" : "Disabled"));
                        lines.add("&6Active Games: &f" + plugin.getGameManager().getActiveGames().size());
                    }
                    break;
                case LOBBY_QUEUE:
                    GameQueue queue = gameQueueManager.getQueue(player);
                    GameQueueMember queueMember = gameQueueManager.findQueueMember(queue, player.getUniqueId());
                    boolean ranked = queue.getType().equals(GameQueue.Type.RANKED);
                    lines.add("&6Online: &f" + Bukkit.getOnlinePlayers().size());
                    lines.add("&6In Game: &f" + gameManager.getTotalInGame());
                    lines.add(" ");
                    lines.add("&6In Queue:");
                    lines.add(" &7● &6" + queue.getDuelKit().getDisplayName() + (ranked ? " &f&l(R)" : " &f(U)"));

                    if(queue.getType().equals(GameQueue.Type.RANKED)) {
                        lines.add(" &7● &6ELO: &f" + profile.getProfileElo().getRatings().get(queue.getDuelKit()));
                        lines.add(" &7● &6Range: &f" + queueMember.getEloLow() + " - " + queueMember.getEloHigh());
                    }

                    if(showDuration) {
                        lines.add(" &7● &f" + TimeUtil.get(new Date(), queueMember.getJoined()));
                    }
                    break;
                case LOBBY_PARTY:
                    lines.add("&6Online: &f" + Bukkit.getOnlinePlayers().size());
                    lines.add("&6In Game: &f" + gameManager.getTotalInGame());
                    lines.add(" ");
                    Party party = profile.getParty();
                    PartyMember member = party.getMembers().get(player.getUniqueId());
                    HCFKit kit = member.getHcfKit();
                    lines.add("&6Party &7(" + party.getMembers().size() + ")");
                    lines.add("&6Leader: &f" + party.getLeader().getName());
                    lines.add("&6Your HCF Kit: " + kit.getColor() + kit.toString());

                    if(party.getGame() != null) {
                        lines.add("&7&oParty In Game");
                    }
                    break;
                case LOBBY_TOURNAMENT:
                    Tournament tournament = profile.getTournament();
                    lines.addAll(tournament.getScoreboard(profile));
                    break;
                case IN_GAME:
                    Game game = profile.getGame();

                    if(game.getScoreboard(profile) == null) {
                        return null;
                    }

                    lines.addAll(game.getScoreboard(profile));
                    break;
                case SPECTATING:
                    game = profile.getGame();

                    if(game.getSpectatorScoreboard(profile) == null) {
                        return null;
                    }

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

            if(profile.getArenaCopier() != null) {
                ArenaCopier act = profile.getArenaCopier();
                lines.add(" ");
                lines.add("&6&lArenaCopyTask");
                lines.add(" &7● &6Source: &f" + act.getArena().getName());
                lines.add(" &7● &6Copy: &f" + act.getNewArena().getName());
                lines.add(" &7● &6Difference: &fX" + act.getXDifference() + " Z" + act.getZDifference());
                lines.add(" &7● &6Blocks Left: &f" + act.getBlocks().size());
                lines.add(" &7● &6Duration: &f" + TimeUtil.get(act.getStarted()));
            }

            if(staff) {
                ArenaBlockUpdater abu = plugin.getArenaManager().getArenaBlockUpdater();
                if(abu != null && abu.getEnded() == 0) {
                    lines.add(" ");
                    lines.add("&6&lArenaBlockUpdater");
                    lines.add(" &7● &6Source: &f" + abu.getArena().getName());
                    lines.add(" &7● &6Blocks Left: &f" + abu.getBlocks().size());
                    lines.add(" &7● &6Duration: &f" + TimeUtil.get(abu.getStarted()));
                }

                ArenaCopyQueue acu = plugin.getArenaManager().getArenaCopyQueue();
                if(!acu.getCopyQueue().isEmpty()) {
                    Queue<ArenaCopier> copierQueue = acu.getCopyQueue();
                    ArenaCopier ac = copierQueue.peek();
                    lines.add(" ");
                    lines.add("&6&lArenaCopyQueue &7(" + copierQueue.size() + ")");
                    lines.add(" &7● &6Source: &f" + ac.getArena().getName());
                    lines.add(" &7● &6Current: &f" + ac.getNewArena().getName());
                    lines.add(" &7● &6Blocks Left: &f" + ac.getBlocks().size());
                    lines.add(" &7● &6Duration: &f" + TimeUtil.get(ac.getStarted()));
                }

                ArenaDeleter ad = plugin.getArenaManager().getArenaDeleter();
                if(ad != null && ad.getEnded() == 0) {
                    lines.add(" ");
                    lines.add("&6&lArenaDeleter");
                    lines.add(" &7● &6Source: &f" + ad.getArena().getName());
                    lines.add(" &7● &6Blocks Left: &f" + ad.getBlocks().size());
                    lines.add(" &7● &6Duration: &f" + TimeUtil.get(ad.getStarted()));
                }
            }

            lines.add(" ");

            lines.add(plugin.getConfig().getString("scoreboard.ip"));

            if(showLines) {
                lines.add("&7&m------------------");
            }
        }

        return lines;
    }
}
