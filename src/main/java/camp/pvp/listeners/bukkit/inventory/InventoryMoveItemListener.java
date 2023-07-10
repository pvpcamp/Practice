package camp.pvp.listeners.bukkit.inventory;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.profiles.GameProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;

public class InventoryMoveItemListener implements Listener {

    private Practice plugin;
    public InventoryMoveItemListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        Inventory source = event.getSource();
        Inventory destination = event.getDestination();

        if(source.getHolder() instanceof Player) {
            Player player = (Player) source.getHolder();
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            Game game = profile.getGame();

            if(game != null) {
                if(!game.getCurrentPlaying().contains(player)) {
                    event.setCancelled(true);
                }
            } else if(!profile.isBuildMode()) {
                event.setCancelled(true);
            }
        }
    }
}
