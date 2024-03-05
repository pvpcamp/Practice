package camp.pvp.practice.listeners.bukkit.entity;

import camp.pvp.practice.Practice;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.map.MapView;

import java.net.InetSocketAddress;

public class EntityExplodeListener implements Listener {

    private Practice plugin;
    public EntityExplodeListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {

        if(event.getEntityType().equals(EntityType.PRIMED_TNT)) {
            event.blockList().clear();
            return;
        }

        if(event.getEntityType().equals(EntityType.FIREBALL)) {
            event.blockList().clear();
        }
    }
}
