package camp.pvp.listeners.bukkit.player;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.profiles.GameProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {

    private Practice plugin;
    public FoodLevelChangeListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            Game occupation = profile.getGame();
            if(occupation != null) {
                if(occupation.getCurrentPlaying().contains(player)) {
                    if (occupation.getKit() != null && !occupation.getKit().isHunger()) {
                        event.setCancelled(true);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}
