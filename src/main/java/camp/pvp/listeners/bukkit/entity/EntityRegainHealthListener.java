package camp.pvp.listeners.bukkit.entity;

import camp.pvp.Practice;
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
        // TODO: Hardcore kits. (classic, builduhc, etc.)
//        if(event.getEntity() instanceof Player) {
//            Player player = (Player) event.getEntity();
//            Profile profile = module.getProfileManager().get(player.getUniqueId());
//            if(event.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED) && profile.getOccupation() != null) {
//                Occupation occupation = profile.getOccupation();
//                if(occupation.getKit() != null && !occupation.getKit().isRegen()) {
//                    event.setCancelled(true);
//                }
//            }
//        }
    }
}
