package camp.pvp.listeners.bukkit.player;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.profiles.GameProfile;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerPickupItemListener implements Listener {

    private Practice plugin;
    public PlayerPickupItemListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        Game game = profile.getGame();
        Item item = event.getItem();

        if(game != null) {
            if(game.getCurrentPlaying().contains(player)) {
                if (!plugin.getEntityHider().canSee(player, item)) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        } else if(!profile.isBuildMode()){
            event.setCancelled(true);
        }
    }
}
