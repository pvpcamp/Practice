package camp.pvp.practice.kits.impl;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.kits.BaseDuelKit;
import camp.pvp.practice.kits.GameKit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class BedFightKit extends BaseDuelKit {
    public BedFightKit() {
        super(GameKit.BED_FIGHT);

        setBuild(true);
        setBedwars(true);
        setRespawn(true);
        setApplyLeatherTeamColor(true);
        setHunger(false);
        setMoveOnStart(false);
        setItemDurability(false);
        setFallDamage(false);

        setIcon(new ItemStack(Material.BED));

        setArenaTypes(Collections.singletonList(Arena.Type.DUEL_BED_FIGHT));

        ItemStack[] armor = getArmor(), inv = getItems();

        armor[3] = new ItemStack(Material.LEATHER_HELMET);

        armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);

        armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);

        armor[0] = new ItemStack(Material.LEATHER_BOOTS);

        inv[0] = new ItemStack(Material.WOOD_SWORD);

        inv[1] = new ItemStack(Material.WOOD_PICKAXE);
        inv[1].addEnchantment(Enchantment.DIG_SPEED, 1);

        inv[2] = new ItemStack(Material.WOOD_AXE);
        inv[2].addEnchantment(Enchantment.DIG_SPEED, 1);

        inv[3] = new ItemStack(Material.SHEARS);
        inv[4] = new ItemStack(Material.WOOL, 64);
    }
}
