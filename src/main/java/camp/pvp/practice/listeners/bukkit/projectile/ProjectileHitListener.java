package camp.pvp.practice.listeners.bukkit.projectile;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
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
        if(!(event.getEntity().getShooter() instanceof Player)) return;

        Entity entity = event.getEntity();
        Player player = (Player) event.getEntity().getShooter();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        Game game = profile.getGame();

        if(game != null) {
            game.addEntity(entity);

            if(entity instanceof Arrow arrow && !game.getKit().isArrowPickup()) {
                arrow.remove();
            }
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                () -> plugin.getProtocolManager().removePacketListener(particleListener), 2L);
    }
}
