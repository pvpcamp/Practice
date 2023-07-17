package camp.pvp.practice.listeners.bukkit.potion;

import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.profiles.GameProfileManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;

import java.util.ArrayList;
import java.util.List;

public class PotionSplashListener implements Listener {

    private Practice plugin;
    public PotionSplashListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion thrownPotion = event.getPotion();
        List<LivingEntity> remove = new ArrayList<>();
        if(thrownPotion.getShooter() instanceof Player) {

            Player player = (Player) thrownPotion.getShooter();
            GameProfileManager gpm = plugin.getGameProfileManager();
            GameProfile profile = gpm.getLoadedProfiles().get(player.getUniqueId());
            Game game = profile.getGame();

            if(game != null) {
                if(game.getParticipants().containsKey(player.getUniqueId())) {
                    GameParticipant participant = game.getParticipants().get(player.getUniqueId());
                    participant.thrownPotions++;
                    if(event.getIntensity(player) < 0.5) {
                        participant.missedPotions++;
                    }
                }
            }

            for(LivingEntity entity : event.getAffectedEntities()) {
                if(entity instanceof Player) {
                    Player p = (Player) entity;
                    GameProfile pr = gpm.getLoadedProfiles().get(p.getUniqueId());;
                    Game g = pr.getGame();

                    if(!p.canSee(player)) {
                        remove.add(entity);
                    }

                    if(g != null) {
                        if(!g.getParticipants().containsKey(p.getUniqueId())) {
                            remove.add(entity);
                        }
                    } else {
                        remove.add(entity);
                    }
                }
            }

            for(LivingEntity entity : remove) {
                event.setIntensity(entity, 0);
            }

            PacketListener particleListener = new PacketAdapter(plugin, PacketType.Play.Server.WORLD_EVENT) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    Player p = event.getPlayer();
                    event.setCancelled(!p.canSee(player));
                }
            };
            plugin.getProtocolManager().addPacketListener(particleListener);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.getProtocolManager().removePacketListener(particleListener), 2L);
        }
    }

}
