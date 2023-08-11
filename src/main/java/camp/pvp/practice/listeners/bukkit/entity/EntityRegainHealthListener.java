package camp.pvp.practice.listeners.bukkit.entity;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class EntityRegainHealthListener implements Listener {

    private Practice plugin;
    public EntityRegainHealthListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            if(event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED) && profile.getGame() != null) {
                Game game = profile.getGame();
                if(game.getKit() != null && !game.getKit().isRegen()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
