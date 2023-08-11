package camp.pvp.practice.listeners.bukkit.block;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ModifiedBlock;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
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
        Game game = gameProfile.getGame();

        if(gameProfile.isBuildMode()) {
            return;
        }

        if(game != null && game.isBuild() && game.getState().equals(Game.State.ACTIVE)) {
            if(game.getCurrentPlayersPlaying().contains(player)) {
                if(game.isInBorder(block.getLocation())) {
                    Arena arena = game.getArena();
                    if(arena.getType().canModifyArena()) {

                        ModifiedBlock modifiedBlock = null;
                        for(ModifiedBlock mb : game.getArena().getModifiedBlocks()) {
                            if(mb.getLocation().equals(block.getLocation())) {
                                modifiedBlock = mb;
                            }
                        }

                        if(modifiedBlock == null) {
                            boolean add = true;
                            for(ModifiedBlock mb : arena.getPlacedBlocks()) {
                                if(mb.getLocation().equals(block.getLocation())) {
                                    add = false;
                                    break;
                                }
                            }

                            if(add) {
                                modifiedBlock = new ModifiedBlock(block);
                                game.getArena().getModifiedBlocks().add(modifiedBlock);
                            }
                        }
                    } else {

                        boolean b = false;
                        for(ModifiedBlock mb : arena.getPlacedBlocks()) {
                            if(mb.getLocation().equals(block.getLocation())) {
                                b = true;
                                break;
                            }
                        }

                        if(!b) {
                            event.setCancelled(true);
                        }
                        return;
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
