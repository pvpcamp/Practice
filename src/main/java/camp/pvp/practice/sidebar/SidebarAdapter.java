package camp.pvp.practice.sidebar;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameManager;
import camp.pvp.practice.games.impl.Duel;
import camp.pvp.practice.games.sumo.SumoEvent;
import camp.pvp.practice.games.sumo.SumoEventDuel;
import camp.pvp.practice.games.tournaments.Tournament;
import camp.pvp.practice.kits.CustomGameKit;
import camp.pvp.practice.kits.GameKit;
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

public class SidebarAdapter implements AssembleAdapter {

    private static String TITLE;
    private static String LINE;

    private Practice plugin;
    private GameManager gameManager;
    private GameProfileManager gameProfileManager;
    private GameQueueManager gameQueueManager;
    public SidebarAdapter(Practice plugin) {
        TITLE = plugin.getConfig().getString("scoreboard.title");
        LINE = "&7&m------------------";

        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        this.gameProfileManager = plugin.getGameProfileManager();
        this.gameQueueManager = plugin.getGameQueueManager();
    }

    @Override
    public String getTitle(Player player) {

        StringBuilder sb = new StringBuilder();
        sb.append(TITLE);

        GameProfile profile = gameProfileManager.getLoadedProfile(player.getUniqueId());

        switch(profile.getState()) {
            case IN_GAME, SPECTATING -> {
                sb.append(" &7❘ &f");
                sb.append(profile.getGame().getScoreboardTitle());
            }
        }

        return sb.toString();
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        GameProfile profile = gameProfileManager.getLoadedProfiles().get(player.getUniqueId());
        if(profile != null && profile.isShowSidebar()) {
            GameProfile.State state = profile.getState();
            boolean showDuration = profile.isSidebarShowDuration(),
                    showLines = profile.isSidebarShowLines(),
                    staff = player.hasPermission("practice.staff");

            if(showLines) {
                lines.add(LINE);
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

                    lines.add("&6Online: &f" + online + (staff ? " &7(" + staffOnline + " Staff)" : ""));
                    lines.add("&6In Game: &f" + inGame);

                    if(staff) lines.add("&6Staff Mode: &f" + (profile.isStaffMode() ? "Enabled" : "Disabled"));

                    break;
                case LOBBY_QUEUE:
                    GameQueue queue = gameQueueManager.getQueue(player);
                    GameQueueMember queueMember = gameQueueManager.findQueueMember(queue, player.getUniqueId());

                    boolean ranked = queue.getType().equals(GameQueue.Type.RANKED);

                    lines.add("&6Online: &f" + Bukkit.getOnlinePlayers().size());
                    lines.add("&6In Game: &f" + gameManager.getTotalInGame());
                    lines.add(" ");

                    switch(queue.getGameType()) {
                        case DUEL -> {
                            lines.add("&6In Duel Queue:");
                            lines.add(" &7● " + queue.getType().getColor() + queue.getGameKit().getDisplayName() + (ranked ? " &f&l(R)" : " &f(U)"));

                            if(queue.getType().equals(GameQueue.Type.RANKED)) {
                                lines.add(" &7● &6ELO: &f" + profile.getProfileElo().getRatings().get(queue.getGameKit()));
                                lines.add(" &7● &6Range: &f" + queueMember.getEloLow() + " - " + queueMember.getEloHigh());
                            }
                        }
                        case MINIGAME -> {
                            lines.add("&6In Minigame Queue:");
                            lines.add(" &7● " + queue.getType().getColor() + queue.getMinigameType().toString() + (ranked ? " &f&l(R)" : " &f(U)"));
                            lines.add(" &7● &6In Queue: &f" + queue.getQueueMembers().size());

                            if(queue.isCountdown()) {
                                lines.add(" &7● &6Starting In: &f" + queue.getTimeBeforeStart() + "s");
                            } else {
                                lines.add("&7&oWaiting for players.");
                            }
                        }
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
                    lines.add("&6Party &7(" + party.getMembers().size() + "/" + party.getMaxMembers() + ")");
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
                case LOBBY_EVENT:
                    SumoEvent event = profile.getSumoEvent();
                    lines.addAll(event.getScoreboard(profile));
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
                    GameKit editingKit = profile.getEditingKit();

                    lines.add("&6Editing Kit:");
                    lines.add(" &f" + editingKit.getDisplayName());
                    lines.add(" ");
                    lines.add("&6Sign: &fLeave");

                    if(editingKit.getMoreItems() != null) {
                        lines.add("&6Chest: &fMore Items");
                    }

                    lines.add("&6Anvil: &fSave");
                    break;
                default:
                    lines.add("&f&oIn Development.");
            }

            lines.add(" ");

            lines.add(plugin.getConfig().getString("scoreboard.ip") + (profile.isDebugMode() ? " &8&o(Debug)" : ""));

            if(showLines) {
                lines.add(LINE);
            }
        }

        return lines;
    }
}
