package camp.pvp.practice.listeners.bukkit.block;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private Practice plugin;
    public BlockPlaceListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        Block block = event.getBlock();
        Game game = profile.getGame();

        if(game != null && game.isBuild() && game.getState().equals(Game.State.ACTIVE)) {
            if(game.getCurrentPlayersPlaying().contains(player)) {

                if(game.getArena().getBuildLimit() < block.getLocation().getBlockY()) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You have reached the build limit.");
                    return;
                }

                if(block.getType().equals(Material.TNT)) {
                    TNTPrimed tntPrimed = (TNTPrimed) block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
                    tntPrimed.setFuseTicks(40);

                    block.setType(Material.AIR);
                    game.addEntity(tntPrimed);
                }
            }
        } else {
            if(profile.isBuildMode()) {
                return;
            }

            event.setCancelled(true);
        }
    }
}
