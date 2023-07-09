package camp.pvp.listeners.bukkit.block;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.profiles.GameProfile;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private Practice plugin;
    public BlockBreakListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        GameProfile gameProfile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        Block block = event.getBlock();
        Game game = gameProfile.getGame();

        if(gameProfile.isBuildMode()) {
            return;
        }

//        if(game != null) {
//            // TODO: Build games
//            if(game.isBuild()) {
//
//            }
//        }

        event.setCancelled(true);
    }
}
