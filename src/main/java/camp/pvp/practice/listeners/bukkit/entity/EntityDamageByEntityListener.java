package camp.pvp.practice.listeners.bukkit.entity;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityListener implements Listener {

    private Practice plugin;
    public EntityDamageByEntityListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player player = null;
        Player attacker = null;
        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
            attacker = (Player) event.getDamager();
        }

        if(event.getDamager() instanceof Arrow && event.getEntity() instanceof Player) {
            Arrow arrow = (Arrow) event.getDamager();
            player = (Player) event.getEntity();
            if(arrow.getShooter() instanceof Player) {
                attacker = (Player) arrow.getShooter();
                attacker.sendMessage(ChatColor.WHITE + player.getName() + ChatColor.GREEN + " is now at " + ChatColor.WHITE + Math.round(player.getHealth()) + " HP" + ChatColor.GREEN + ".");
            }
        }

        if(player != null && attacker != null) {
            GameProfile playerProfile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            GameProfile attackerProfile = plugin.getGameProfileManager().getLoadedProfiles().get(attacker.getUniqueId());
            Game game = attackerProfile.getGame();
            if(game != null && playerProfile.getGame() != null) {
                game.handleHit(player, attacker, event);
            } else {
                event.setCancelled(true);
            }
        }
    }
}
