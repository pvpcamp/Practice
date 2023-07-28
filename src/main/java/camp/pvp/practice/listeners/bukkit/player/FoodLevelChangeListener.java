package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
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
            Game game = profile.getGame();
            if(game != null) {
                if(game.getAllPlayers().contains(player)) {
                    if (game.getKit() != null && !game.getKit().isHunger()) {
                        event.setCancelled(true);
                    }
                }

                if(game.getSpectators().get(player.getUniqueId()) != null) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}
