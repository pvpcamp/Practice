package camp.pvp.practice.listeners.bukkit.entity;

import camp.pvp.practice.Practice;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener implements Listener {

    private Practice plugin;
    public EntityExplodeListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        if(event.getEntityType().equals(EntityType.PRIMED_TNT)) {
            TNTPrimed tnt = (TNTPrimed) entity;
            event.blockList().clear();
        }
    }
}
