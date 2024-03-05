package camp.pvp.practice.listeners.bukkit.explosion;

import camp.pvp.practice.Practice;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.util.Vector;

public class ExplosionPrimeListener implements Listener {

    private static final double FIREBALL_VELOCITY = 1.25;
    private static final double FIREBALL_VERTICAL = 1.25;
    private static final double NEARBY_RANGE = 3.0;

    private Practice plugin;
    public ExplosionPrimeListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {

        if(!(event.getEntity() instanceof Fireball fireball && fireball.getTicksLived() < 20)) return;

        for(Entity entity : fireball.getNearbyEntities(NEARBY_RANGE, NEARBY_RANGE, NEARBY_RANGE)) {
            if(entity instanceof Player player && fireball.getShooter().equals(player)) {

                Vector vector = player.getLocation().getDirection();

                vector.setY(0).normalize();

                vector.multiply(FIREBALL_VELOCITY);

                vector.setY(FIREBALL_VERTICAL);

                player.setVelocity(vector);

                return;
            }
        }
    }
}
