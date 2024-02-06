package camp.pvp.practice.profiles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerVisibilityUpdater implements Runnable{

    private GameProfileManager gpm;
    public PlayerVisibilityUpdater(GameProfileManager gpm) {
        this.gpm = gpm;
    }

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            GameProfile source = gpm.getLoadedProfiles().get(player.getUniqueId());

            if(source == null) {
                player.kickPlayer(ChatColor.RED + "An error occurred while loading your profile. Please rejoin.");
                continue;
            }

            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p != player) {
                    if(source.getHiddenPlayers().contains(p.getUniqueId())) {
                        if(player.canSee(p)) {
                            player.hidePlayer(p);
                        }
                    } else {
                        if(!player.canSee(p)) {
                            player.showPlayer(p);
                        }
                    }
                }
            }
        }
    }
}
