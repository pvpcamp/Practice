package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.Practice;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleportListener implements Listener {

    private Practice plugin;
    public PlayerTeleportListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            Location location = event.getTo();
            location.setX(location.getBlockX() + 0.5);
            location.setY(location.getBlockY() + 0.2);
            location.setZ(location.getBlockZ() + 0.5);
            event.setTo(location);
        }
    }
}
