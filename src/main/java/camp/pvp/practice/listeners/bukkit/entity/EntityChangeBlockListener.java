package camp.pvp.practice.listeners.bukkit.entity;

import camp.pvp.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EntityChangeBlockListener implements Listener {

    private Practice plugin;
    public EntityChangeBlockListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof FallingBlock) {
            FallingBlock fallingBlock = (FallingBlock) entity;
            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                if(!fallingBlock.isValid()) {
                    fallingBlock.remove();
                    fallingBlock.getLocation().getBlock().setType(Material.AIR);
                }
            }, 1);
        }
    }
}
