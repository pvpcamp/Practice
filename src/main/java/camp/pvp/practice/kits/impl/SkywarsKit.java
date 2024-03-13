package camp.pvp.practice.kits.impl;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.kits.BaseDuelKit;
import camp.pvp.practice.kits.GameKit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class SkywarsKit extends BaseDuelKit {
    public SkywarsKit() {
        super(GameKit.SKYWARS);

        setBuild(true);
        setMoveOnStart(false);
        setShowHealthBar(true);

        setArenaTypes(Collections.singletonList(Arena.Type.DUEL_SKYWARS));

        setIcon(new ItemStack(Material.EYE_OF_ENDER));

        ItemStack[] armor = getArmor(), inv = getItems();

        armor[3] = new ItemStack(Material.IRON_HELMET);
        armor[2] = new ItemStack(Material.IRON_CHESTPLATE);
        armor[1] = new ItemStack(Material.IRON_LEGGINGS);
        armor[0] = new ItemStack(Material.IRON_BOOTS);

        inv[0] = new ItemStack(Material.IRON_SWORD);
        inv[1] = new ItemStack(Material.IRON_PICKAXE);
        inv[2] = new ItemStack(Material.IRON_AXE);
        inv[3] = new ItemStack(Material.COOKED_BEEF, 16);
    }
}
