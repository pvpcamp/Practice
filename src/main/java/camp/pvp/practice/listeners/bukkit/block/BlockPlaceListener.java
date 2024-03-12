package camp.pvp.practice.listeners.bukkit.block;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

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
        Material material = block.getType();
        Game game = profile.getGame();

        if(game != null && game.isBuild() && game.getAlivePlayers().contains(player)) {
            if(game.getArena().getBuildLimit() < block.getLocation().getBlockY()) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You have reached the build limit.");
                return;
            }

            if(material.equals(Material.TNT) && !game.getKit().canPlaceTntBeforeStart() && !game.getState().equals(Game.State.ACTIVE)) {
                player.sendMessage(ChatColor.RED + "You cannot place TNT before the game starts.");
                event.setCancelled(true);
                return;
            }

            if(!game.getKit().canPlaceBlocksBeforeStart() && !game.getState().equals(Game.State.ACTIVE) && !material.equals(Material.TNT)) {
                player.sendMessage(ChatColor.RED + "You cannot place blocks before the game starts.");
                event.setCancelled(true);
                return;
            }

            if(block.getType().equals(Material.TNT)) {
                Location location = block.getLocation();
                location.add(0.5, 0.5, 0.5);
                TNTPrimed tntPrimed = (TNTPrimed) block.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
                tntPrimed.setFuseTicks(60);

                Vector velocity = new Vector(0, 0.25, 0);
                tntPrimed.setVelocity(velocity);

                block.setType(Material.AIR);
                game.addEntity(tntPrimed);
            }
        } else {
            if(profile.isBuildMode()) {
                return;
            }

            event.setCancelled(true);
        }
    }
}
