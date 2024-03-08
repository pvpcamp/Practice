package camp.pvp.practice.listeners.bukkit.entity;

import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.utils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class EntityDamageByEntityListener implements Listener {

    private Practice plugin;
    public EntityDamageByEntityListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player attacker = null;

        if(event.getEntity().hasMetadata("NPC")) {
            event.setCancelled(true);
            return;
        }

        if(event.getEntity() instanceof Fireball && event.getDamager() instanceof Player player) {
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfile(player.getUniqueId());
            Game game = profile.getGame();
            if(!game.getAlive().containsKey(player.getUniqueId())) {
                event.setCancelled(true);
            }
            return;
        }

        if(!(event.getEntity() instanceof Player player)) return;

        if(event.getDamager() instanceof Player) attacker = (Player) event.getDamager();

        if(event.getDamager() instanceof FishHook && event.getEntity() instanceof Player) {
            FishHook fishHook = (FishHook) event.getDamager();
            if(fishHook.getShooter() instanceof Player) {
                attacker = (Player) fishHook.getShooter();
            }
        }

        if(event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player) attacker = (Player) arrow.getShooter();

        if(event.getDamager() instanceof Fireball || event.getDamager() instanceof TNTPrimed) {

            GameProfile profile = plugin.getGameProfileManager().getLoadedProfile(player.getUniqueId());
            Game game = profile.getGame();

            if(game != null && game.getKit().equals(GameKit.FIREBALL_FIGHT) && game.getAlive().containsKey(player.getUniqueId())) {

                double damage = 5;

                if (event.getDamager().getTicksLived() < 20) {
                    damage = 0;
                } else {
                    damage = event.getFinalDamage() / 4;
                }

                player.damage(damage);

                Location location = event.getDamager().getLocation().subtract(0, -1, 0);
                Vector dirToExplosion = location.toVector().subtract(player.getLocation().toVector());

                // Invert direction.
                dirToExplosion.multiply(-1);
                // Normalize the vector.
                dirToExplosion.setY(0).normalize();

                // Multiply the vector to get desired explosion strength.
                dirToExplosion.multiply(1.25);
                // Set Y to make the player fly up.
                dirToExplosion.setY(1.20);

                player.setVelocity(dirToExplosion);

                event.setCancelled(true);
            }
        }

        if(attacker != null) {
            GameProfile playerProfile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            GameProfile attackerProfile = plugin.getGameProfileManager().getLoadedProfiles().get(attacker.getUniqueId());
            Game game = attackerProfile.getGame();

            if(game != null && playerProfile.getGame() != null) {

                if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                    if(attackerProfile.getCps() > 20) {
                        event.setCancelled(true);
                        return;
                    }
                }

                game.handleHit(player, attacker, event);
            } else {
                if(attacker.getPassenger() != null && attacker.getPassenger().equals(player)) {
                    player.eject();
                    attacker.eject();

                    Location location = attacker.getLocation();
                    Vector dir = location.getDirection();
                    Vector vector = new Vector(dir.getX(), dir.getY(), dir.getZ());

                    vector.multiply(2);
                    player.setVelocity(vector);

                    player.playSound(location, Sound.EXPLODE, 1F, 1F);
                    attacker.playSound(location, Sound.EXPLODE, 1F, 1F);
                }

                event.setCancelled(true);
            }
        }
    }
}
