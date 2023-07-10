package camp.pvp.listeners.bukkit.projectile;

import camp.pvp.Practice;
import camp.pvp.cooldowns.PlayerCooldown;
import camp.pvp.games.Game;
import camp.pvp.games.GameParticipant;
import camp.pvp.profiles.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;

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

            if(game != null) {

                game.addEntity(event.getEntity());
                GameParticipant participant = game.getAlive().get(player.getUniqueId());

                if(event.getEntity() instanceof EnderPearl && participant != null) {

                    if(game.getState().equals(Game.State.ACTIVE)) {

                        PlayerCooldown cooldown = participant.getCooldowns().get(PlayerCooldown.Type.ENDER_PEARL);

                        if (cooldown != null && !cooldown.isExpired()) {

                            event.setCancelled(true);

                        } else {

                            cooldown = new PlayerCooldown(PlayerCooldown.Type.ENDER_PEARL, participant, player);
                            participant.getCooldowns().put(PlayerCooldown.Type.ENDER_PEARL, cooldown);

                        }
                    } else {

                        event.setCancelled(true);
                        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));

                    }
                }
            }
        }
    }
}
