package camp.pvp.listeners.bukkit.player;

import camp.pvp.Practice;
import camp.pvp.profiles.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocessListener implements Listener {

    private Practice plugin;
    public PlayerCommandPreprocessListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(profile == null) {
            player.sendMessage(ChatColor.RED + "Your Practice profile has not been loaded yet, please wait.");
            event.setCancelled(true);
        }
    }
}
