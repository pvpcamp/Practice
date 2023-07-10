package camp.pvp.listeners.bukkit.inventory;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.profiles.GameProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    private Practice plugin;
    public InventoryClickListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        switch(profile.getState()) {
            case KIT_EDITOR:
                break;
            case IN_GAME:
                Game occupation = profile.getGame();
                if(occupation == null) {
//                    Participant participant = occupation.getAlive().get(player.getUniqueId());
//                    if(participant != null && !participant.isKitApplied()) {
//                        event.setCancelled(true);
//                    }
                }
                break;
            default:
                if(!profile.isBuildMode()) {
                    event.setCancelled(true);
                }
        }
    }
}
