package camp.pvp.practice.listeners.bukkit.block;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ModifiedBlock;
import camp.pvp.practice.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class BlockFromToListener implements Listener {

    private Practice plugin;
    public BlockFromToListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block from = event.getBlock();
        Block to = event.getToBlock();

        boolean cancel = true;
        for(Game game : plugin.getGameManager().getActiveGames()) {
            if(game.isBuild() && game.getArena() != null && game.getState().equals(Game.State.ACTIVE)) {
                Arena arena = game.getArena();
                if(arena.isPlacedBlock(from.getLocation()) || arena.isOriginalBlock(from.getLocation())) {
                    arena.addBlock(to);
                    cancel = false;
                }
            }
        }

        if(cancel) {
            event.setCancelled(true);
        }
    }
}
