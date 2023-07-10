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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public enum DeathAnimation {
    DEFAULT, BLOOD, CLOUD;

    public void playAnimation(Game game, Location location) {
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

                game.playSound(location, Sound.SUCCESSFUL_HIT, 1F, 1F);

                Bukkit.getScheduler().runTaskLater(Practice.instance, ()-> {
                    for(Item i : items) {
                        i.remove();
                    }
                }, 40);

                break;
            default:
                break;
        }
    }
}
