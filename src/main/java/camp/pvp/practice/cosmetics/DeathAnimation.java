package camp.pvp.practice.cosmetics;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public enum DeathAnimation {
    DEFAULT, BLOOD, EXPLOSION, ENDERMAN;

    public String toString() {
        switch(this) {
            case BLOOD:
                return "Blood";
            case EXPLOSION:
                return "Explosion";
            case ENDERMAN:
                return "Enderman";
            default:
                return "Default (Lightning)";
        }
    }

    public ItemStack getIcon() {
        switch(this) {
            case BLOOD:
                return new ItemStack(Material.REDSTONE_BLOCK);
            case EXPLOSION:
                return new ItemStack(Material.SULPHUR);
            case ENDERMAN:
                return new ItemStack(Material.ENDER_PEARL);
            default:
                return new ItemStack(Material.BLAZE_ROD);
        }
    }

    public void playAnimation(Game game, Player victim, Location location, boolean velocity) {
        World world = location.getWorld();
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

                game.playSound(location, Sound.HURT_FLESH, 1F, 1F);
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

                game.playSound(location, Sound.HURT_FLESH, 1F, 1F);
                game.playSound(location, Sound.EXPLODE, 1F, 1F);

                break;
            case ENDERMAN:
                l = new Location(world, location.getX(), location.getY() + 1, location.getZ());
                if(velocity) {
                    victim.setVelocity(new Vector(0, 0.5, 0));
                }

                game.playEffect(l, Effect.ENDER_SIGNAL, null);
                game.playSound(location, Sound.ENDERMAN_HIT, 1F, 1F);
                break;
            default:
                l = new Location(world, location.getX(), location.getY() + 0.5, location.getZ());
                if(velocity) {
                    victim.setVelocity(new Vector(0, 0.5, 0));
                }

                game.playLightning(l);
                break;
        }
    }
}
