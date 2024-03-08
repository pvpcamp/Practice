package camp.pvp.practice.listeners.bukkit.projectile;

import camp.pvp.practice.cooldowns.PlayerCooldown;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class ProjectileLaunchListener implements Listener {

    private Practice plugin;
    public ProjectileLaunchListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if(event.getEntity().getShooter() instanceof Player) {

            Player player = (Player) event.getEntity().getShooter();
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            Game game = profile.getGame();

            for(Player p : Bukkit.getOnlinePlayers()) {
                if(!p.canSee(player)) {
                    plugin.getEntityHider().hideEntity(p, event.getEntity());
                }
            }

            if(game == null) return;

            game.addEntity(event.getEntity());
            GameParticipant participant = game.getAlive().get(player.getUniqueId());

            if(participant == null) {
                event.setCancelled(true);
                return;
            }

            if(event.getEntity() instanceof EnderPearl) {

                if(game.getState().equals(Game.State.ACTIVE)) {

                    PlayerCooldown cooldown = participant.getCooldowns().get(PlayerCooldown.Type.ENDER_PEARL);

                    if (cooldown != null && !cooldown.isExpired()) {

                        event.setCancelled(true);
                        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));

                    } else {

                        cooldown = new PlayerCooldown(PlayerCooldown.Type.ENDER_PEARL, participant, player);
                        participant.getCooldowns().put(PlayerCooldown.Type.ENDER_PEARL, cooldown);

                    }
                } else {

                    event.setCancelled(true);
                    player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));

                }

                return;
            }

            if(event.getEntity() instanceof ThrownPotion potion) {
                for(PotionEffect effect : potion.getEffects()) {
                    if(effect.getType().equals(PotionEffectType.HEAL)) {
                        participant.thrownPotions++;
                        return;
                    }
                }
                return;
            }

            if(event.getEntity() instanceof Arrow) {
                participant.arrowShots++;
                return;
            }

            if(event.getEntity() instanceof Fireball) {
                participant.fireballShots++;
                return;
            }
        }
    }
}
