package camp.pvp.practice.listeners.bukkit.block;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
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

        for(Arena arena : plugin.getArenaManager().getArenas()) {
            if(arena.getPlacedBlocks().contains(from)) {
                arena.getPlacedBlocks().add(to);
            }
        }
    }
}
