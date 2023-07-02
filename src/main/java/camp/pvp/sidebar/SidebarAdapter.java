package camp.pvp.sidebar;

import camp.pvp.Practice;
import camp.pvp.profiles.GameProfile;
import camp.pvp.profiles.GameProfileManager;
import io.github.thatkawaiisam.assemble.AssembleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SidebarAdapter implements AssembleAdapter {

    private Practice plugin;
    private GameProfileManager gameProfileManager;
    public SidebarAdapter(Practice plugin) {
        this.plugin = plugin;
        this.gameProfileManager = plugin.getGameProfileManager();
    }

    @Override
    public String getTitle(Player player) {
        return "&6&lPvP Camp &fDev";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

//        GameProfile profile = gameProfileManager.find(player.getUniqueId(), true);
//        if(profile != null) {
//            GameProfile.State state = gameProfileManager.getState(profile);
//
//            lines.add("&7&m------------------");
//            switch(state) {
//                case LOBBY:
//                    lines.add("&6Online: &f" + Bukkit.getOnlinePlayers().size());
//                    return lines;
//                default:
//                    lines.add("&f&oIn Development.");
//            }
//            lines.add(" ");
//            lines.add("&6pvp.camp");
//            lines.add("&7&m------------------");
//        }

        return lines;
    }
}
