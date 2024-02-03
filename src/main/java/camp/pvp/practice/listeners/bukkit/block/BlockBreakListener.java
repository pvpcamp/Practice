package camp.pvp.practice.listeners.bukkit.block;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
            if(game.getAlivePlayers().contains(player)) {
                if(game.isInBorder(location)) {
                    Arena arena = game.getArena();
                    Arena.Type type = arena.getType();

                    game.handleBlockBreak(player, block, event);

                    return;
                }
            }
        }

        event.setCancelled(true);
    }
}
