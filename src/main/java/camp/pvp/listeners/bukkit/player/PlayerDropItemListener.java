package camp.pvp.listeners.bukkit.player;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.profiles.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDropItemListener implements Listener {

    private Practice plugin;
    public PlayerDropItemListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        Game game = profile.getGame();

        if(game != null && game.getAlivePlayers().contains(player)) {

            Item item = event.getItemDrop();

            game.addEntity(item);

            if (item.getItemStack().getType().equals(Material.GLASS_BOTTLE) || item.getItemStack().getType().equals(Material.BOWL)) {
                item.remove();
                return;
            }

            new BukkitRunnable() {
                public void run() {
                    item.remove();
                }
            }.runTaskLater(plugin, 400L);
        } else if(!profile.isBuildMode()) {
            event.setCancelled(true);
        }
    }
}
