package camp.pvp.sidebar;

import camp.pvp.Practice;
import io.github.thatkawaiisam.assemble.AssembleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SidebarAdapter implements AssembleAdapter {

    private Practice plugin;
    public SidebarAdapter(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getTitle(Player player) {
        return "&6&lPvP Camp &fDev";
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add("&7&m------------------");
        lines.add("&6Online: &f" + Bukkit.getOnlinePlayers().size());
        lines.add(" ");
        lines.add("&6pvp.camp");
        lines.add("&7&m------------------");
        return lines;
    }
}
