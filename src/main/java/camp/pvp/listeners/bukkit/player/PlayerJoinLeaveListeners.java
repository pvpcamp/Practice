package camp.pvp.listeners.bukkit.player;

import camp.pvp.Practice;
import camp.pvp.profiles.GameProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveListeners implements Listener {

    private Practice plugin;
    public PlayerJoinLeaveListeners(Practice plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        plugin.getGameProfileManager().find(event.getUniqueId(), true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GameProfile profile = plugin.getGameProfileManager().find(player.getUniqueId(), true);

        if(profile == null) {
            profile = plugin.getGameProfileManager().create(player);
        }

        profile.setName(player.getName());

        profile.playerUpdate();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getGameProfileManager().exportToDatabase(player.getUniqueId(), true, false);
    }
}
