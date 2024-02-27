package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import io.github.thatkawaiisam.assemble.AssembleBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerJoinLeaveListeners implements Listener {

    private Practice plugin;
    public PlayerJoinLeaveListeners(Practice plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {

        String name = event.getName();
        UUID uuid = event.getUniqueId();

        if(name == null) event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Invalid username.");

        Player player = Bukkit.getPlayer(event.getUniqueId());
        if(player != null && player.isOnline()) {
            player.kickPlayer(ChatColor.RED + "You have connected from another location.");
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You have connected from another location, please re-login.");
            return;
        }

        plugin.getGameProfileManager().preLogin(uuid, name);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {

        event.setJoinMessage(null);

        Player player = event.getPlayer();

        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(profile == null || System.currentTimeMillis() - profile.getLastLoadFromDatabase() > 5000) {
            player.kickPlayer(ChatColor.RED + "There was an issue loading your profile, please reconnect.");
            return;
        }

        profile.setName(player.getName());
        profile.getProfileElo().setName(player.getName());

        profile.playerUpdate(true);

        plugin.getGameProfileManager().updateGlobalPlayerVisibility();

        plugin.getAssemble().getBoards().put(player.getUniqueId(), new AssembleBoard(player, plugin.getAssemble()));

        if(!player.hasPermission("practice.staff")) profile.setStaffMode(false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        event.setQuitMessage(null);

        if(profile != null) {
            profile.logOff();

            plugin.getAssemble().getBoards().remove(player.getUniqueId());

            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> plugin.getGameProfileManager().logOff(player.getUniqueId()), 2);
        }
    }
}
