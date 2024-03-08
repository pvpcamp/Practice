package camp.pvp.practice.listeners.bukkit.entity;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.games.Game;
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

        Game game = null;
        for(Game g : plugin.getGameManager().getActiveGames()) {
            if(g.getEntities().contains(event.getEntity())) {
                game = g;
                break;
            }
        }

        if (game == null) return;

        if(event.getEntity() instanceof Fireball fireball) {
            fireball.setIsIncendiary(false);
        }

        Arena arena = game.getArena();

        for(Block block : event.blockList()) {
            if((!arena.getType().canModifyArena() && arena.isOriginalBlock(block.getLocation())) || block.getType().equals(Material.BED_BLOCK)) continue;

            block.setType(Material.AIR);
        }

        event.blockList().clear();
    }
}
