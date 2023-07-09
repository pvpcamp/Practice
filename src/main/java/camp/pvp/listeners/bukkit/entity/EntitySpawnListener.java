package camp.pvp.listeners.bukkit.entity;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EntitySpawnListener implements Listener {

    private Practice plugin;
    public EntitySpawnListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if(event.getEntity() instanceof Item) {
            Item item = (Item) event.getEntity();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Game game : plugin.getGameManager().getActiveGames()) {
                    if(game.getEntities().contains(item)) {
                        if(item.getItemStack().getType().equals(Material.GLASS_BOTTLE) || item.getItemStack().getType().equals(Material.BOWL)) {
                            item.remove();
                        }

                        new BukkitRunnable() {
                            public void run() {
                                item.remove();
                            }
                        }.runTaskLater(plugin, 500L);
                    }
                }
            }, 1);
        }
    }
}
