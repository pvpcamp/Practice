package camp.pvp.cosmetics;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public enum DeathAnimation {
    DEFAULT, BLOOD, EXPLOSION;

    public String toString() {
        switch(this) {
            case BLOOD:
                return "Blood";
            case EXPLOSION:
                return "Explosion";
            default:
                return "Default";
        }
    }

    public void playAnimation(Game game, Player victim, boolean velocity) {
        World world = victim.getWorld();
        Location location = victim.getLocation();
        switch(this) {
            case BLOOD:
                Location l = new Location(world, location.getX(), location.getY() + 0.5, location.getZ());

                ItemStack woolItem = new ItemStack(Material.WOOL);
                woolItem.setDurability((short) 14);

                ItemStack dyeItem = new ItemStack(Material.INK_SACK);
                dyeItem.setDurability((short) 1);

                List<Item> items = new ArrayList<>();

                for(int i = 0; i < 8; i++) {
                    Item bloodItem = location.getWorld().dropItemNaturally(l, woolItem);
                    bloodItem.setPickupDelay(Integer.MAX_VALUE);
                    game.addEntity(bloodItem);
                    items.add(bloodItem);
                }

                for(int i = 0; i < 10; i++) {
                    Item bloodItem = location.getWorld().dropItemNaturally(l, dyeItem);
                    bloodItem.setPickupDelay(Integer.MAX_VALUE);
                    game.addEntity(bloodItem);
                    items.add(bloodItem);
                }

                if(velocity) {
                    victim.setVelocity(new Vector(0, 0.5, 0));
                }

                game.playSound(location, Sound.AMBIENCE_THUNDER, 1F, 1F);

                Bukkit.getScheduler().runTaskLater(Practice.instance, ()-> {
                    for(Item i : items) {
                        i.remove();
                    }
                }, 40);

                break;
            case EXPLOSION:
                l = new Location(world, location.getX(), location.getY() + 0.5, location.getZ());

                if(velocity) {
                    victim.setVelocity(new Vector(0, 1.2, 0));
                }

                game.playEffect(l, Effect.EXPLOSION_LARGE, null);
                game.playSound(location, Sound.EXPLODE, 1F, 1F);

                break;
            default:
                if(velocity) {
                    victim.setVelocity(new Vector(0, 0.6, 0));
                }
                game.playSound(location, Sound.FIREWORK_BLAST, 1F, 1F);
                break;
        }
    }
}
