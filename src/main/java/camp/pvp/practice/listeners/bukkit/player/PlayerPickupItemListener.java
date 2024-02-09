package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
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
            if(game.getAlivePlayers().contains(player)) {
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
