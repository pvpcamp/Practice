package camp.pvp.sidebar;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.games.GameManager;
import camp.pvp.profiles.GameProfile;
import camp.pvp.profiles.GameProfileManager;
import camp.pvp.queue.GameQueue;
import camp.pvp.queue.GameQueueManager;
import io.github.thatkawaiisam.assemble.AssembleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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
        return "&6&lPvP Camp";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        GameProfile profile = gameProfileManager.find(player.getUniqueId(), true);
        if(profile != null) {
            GameProfile.State state = gameProfileManager.getState(profile);

            lines.add("&7&m------------------");
            switch(state) {
                case LOBBY:
                    lines.add("&6Online: &f" + Bukkit.getOnlinePlayers().size());
                    lines.add("&6In Game: &f" + gameManager.getTotalInGame());
                    break;
                case LOBBY_QUEUE:
                    GameQueue queue = gameQueueManager.getQueue(player);
                    boolean ranked = queue.getType().equals(GameQueue.Type.UNRANKED);
                    lines.add("&6Online: &f" + Bukkit.getOnlinePlayers().size());
                    lines.add("&6In Game: &f" + gameManager.getTotalInGame());
                    lines.add(" ");
                    lines.add("&6&nIn Queue:");
                    lines.add(" &7● " + queue.getDuelKit().getDisplayName() + (ranked ? " &f(U)" : "&f(R)"));
//                    if(ranked) {
//                        lines.add(" &7● &f(900-1100)");
//                    }
                    break;
                case LOBBY_PARTY:
                    lines.add("&6Online: &f" + Bukkit.getOnlinePlayers().size());
                    lines.add("&6In Game: &f" + gameManager.getTotalInGame());
                    lines.add(" ");
                    lines.add("&6&nParty:");
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
                    lines.add("&6&nKit Editor:");
                    lines.add("&6Door: &fLeave");
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
