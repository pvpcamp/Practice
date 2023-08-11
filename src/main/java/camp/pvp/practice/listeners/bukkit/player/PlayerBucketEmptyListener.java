package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.ModifiedBlock;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerBucketEmptyListener implements Listener {

    private Practice plugin;
    public PlayerBucketEmptyListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        Game game = profile.getGame();
        Block block = event.getBlockClicked();

        if(profile.isBuildMode()) {
            return;
        }

        player.updateInventory();

        if(game != null && game.isBuild()) {
            if(game.getCurrentPlayersPlaying().contains(player)) {
                BlockFace[] blockFaces = {BlockFace.SELF, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
                for (BlockFace blockFace : blockFaces) {
                    Block b = block.getRelative(blockFace, 1);
                    game.getArena().addBlock(b);
                }
//
//                Bukkit.getScheduler().runTaskLater(plugin, () -> {
//                    BlockFace[] blockFaces = {BlockFace.SELF, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
//                    for (BlockFace blockFace : blockFaces) {
//                        Block b = block.getRelative(blockFace, 1);
//                        game.getArena().addBlock(b);
//                    }
//                }, 1);
                return;
            }
        }
        event.setCancelled(true);
    }
}
