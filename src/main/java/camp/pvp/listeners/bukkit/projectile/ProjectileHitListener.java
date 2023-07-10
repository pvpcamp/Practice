package camp.pvp.listeners.bukkit.projectile;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.profiles.GameProfile;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ProjectileHitListener implements Listener {

    private Practice plugin;
    public ProjectileHitListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            Game game = profile.getGame();

            if(game != null) {
                game.addEntity(event.getEntity());
            }

            PacketListener particleListener = new PacketAdapter(plugin, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    PacketContainer packet = event.getPacket();
                    Player p = event.getPlayer();
                    String sound = packet.getStrings().read(0);
                    if(sound.equalsIgnoreCase("random.bowhit")) {
                        event.setCancelled(!p.canSee(player));
                    }
                }
            };

            plugin.getProtocolManager().addPacketListener(particleListener);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.getProtocolManager().removePacketListener(particleListener), 2L);
        }
    }
}
