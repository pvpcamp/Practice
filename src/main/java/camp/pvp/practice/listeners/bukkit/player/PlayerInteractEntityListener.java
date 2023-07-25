package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.profiles.GameProfileManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntityListener implements Listener {

    private Practice plugin;
    public PlayerInteractEntityListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (!entity.hasMetadata("NPC") && entity instanceof Player) {
            Player target = (Player) entity;
            GameProfileManager gpm = plugin.getGameProfileManager();
            GameProfile profile = gpm.getLoadedProfiles().get(player.getUniqueId());
            GameProfile targetProfile = gpm.getLoadedProfiles().get(target.getUniqueId());
            if(player.getItemInHand() == null || (player.getItemInHand() != null && player.getItemInHand().getType().equals(Material.AIR))) {
                if(profile.getState().equals(GameProfile.State.LOBBY) && targetProfile.getState().equals(GameProfile.State.LOBBY)) {
                    player.performCommand("duel " + target.getName());
                } else if(profile.getState().equals(GameProfile.State.SPECTATING) && targetProfile.getState().equals(GameProfile.State.IN_GAME) && player.hasPermission("practice.staff")) {
                    player.openInventory(target.getInventory());
                }
            }
        }
    }
}
