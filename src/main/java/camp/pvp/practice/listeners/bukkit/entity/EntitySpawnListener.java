package camp.pvp.practice.listeners.bukkit.entity;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
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
                if(item.getItemStack().getType().equals(Material.GLASS_BOTTLE)
                        || item.getItemStack().getType().equals(Material.BOWL)
                        || item.getItemStack().getType().equals(Material.BED)) {
                    item.remove();
                    return;
                }

                new BukkitRunnable() {
                    public void run() {
                        item.remove();
                    }
                }.runTaskLater(plugin, 1200L);
            }, 1);
        }
    }
}
