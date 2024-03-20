package camp.pvp.practice.kits.impl;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.kits.BaseKit;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.queue.GameQueue;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class OneInTheChamberKit extends BaseKit {
    public OneInTheChamberKit() {
        super(GameKit.ONE_IN_THE_CHAMBER);

        getGameTypes().add(GameQueue.GameType.MINIGAME);

        setArenaTypes(Collections.singletonList(Arena.Type.MINIGAME_OITC));

        setShowHealthBar(true);
        setHunger(false);
        setMoveOnStart(false);
        setArrowOneShot(true);
        setShowArrowDamage(false);
        setArrowPickup(false);
        setRespawn(true);
        setDropItemsOnDeath(false);
        setItemDurability(false);
        setFallDamage(false);

        setIcon(new ItemStack(Material.ARROW));

        ItemStack[] inv = getItems();

        inv[0] = new ItemStack(Material.WOOD_SWORD);
        inv[1] = new ItemStack(Material.BOW);
        inv[2] = new ItemStack(Material.ARROW, 1);
    }
}
