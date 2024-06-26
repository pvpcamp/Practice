package camp.pvp.practice.listeners.bukkit.entity;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {

    private Practice plugin;
    public EntityDamageListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        boolean isCitizensNPC = entity.hasMetadata("NPC");
        if(!isCitizensNPC && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            if(profile.getState().equals(GameProfile.State.IN_GAME)) {

                Game game = profile.getGame();

                game.handleDamage(player, event);
            } else {
                event.setCancelled(true);
            }
        }
    }
}
