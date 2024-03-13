package camp.pvp.practice.kits.impl;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.kits.BaseDuelKit;
import camp.pvp.practice.kits.NewGameKit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class SpleefKit extends BaseDuelKit {

    public SpleefKit() {
        super(NewGameKit.SPLEEF);

        setIcon(new ItemStack(Material.SNOW_BALL));

        setArenaTypes(Collections.singletonList(Arena.Type.SPLEEF));
        setBuild(true);
        setTeams(false);
        setHunger(false);
        setTakeDamage(false);
        setDieInWater(true);
        setFallDamage(false);

        ItemStack[] inv = getItems();
        inv[0] = new ItemStack(Material.DIAMOND_SPADE);
        inv[0].addEnchantment(Enchantment.DIG_SPEED, 5);
        inv[0].addEnchantment(Enchantment.DURABILITY, 3);
    }
}
