package camp.pvp.practice.listeners.bukkit.block;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ModifiedBlock;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    private Practice plugin;
    public BlockBreakListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        GameProfile gameProfile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        Block block = event.getBlock();
        Location location = block.getLocation();
        Game game = gameProfile.getGame();

        if(gameProfile.isBuildMode()) {
            return;
        }

        if(game != null && game.isBuild() && game.getState().equals(Game.State.ACTIVE)) {
            if(game.getCurrentPlayersPlaying().contains(player)) {
                if(game.isInBorder(location)) {
                    Arena arena = game.getArena();
                    Arena.Type type = arena.getType();
                    if(type.canModifyArena()) {
                        if(type.getSpecificBlocks() != null) {
                            if(!type.getSpecificBlocks().contains(block.getType())) {
                                event.setCancelled(true);
                                return;
                            }
                        }

                        arena.addBlock(block);
                    } else {
                        if(arena.isOriginalBlock(location)) {
                            event.setCancelled(true);
                            return;
                        } else {
                            arena.addBlock(block);
                        }
                    }

                    for (ItemStack item : block.getDrops()) {
                        Item i = block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
                        game.addEntity(i);
                    }

                    block.setType(Material.AIR);

                    return;
                }
            }
        }

        event.setCancelled(true);
    }
}
