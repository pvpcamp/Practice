package camp.pvp.practice.kits.impl;

import camp.pvp.practice.kits.BaseDuelKit;
import camp.pvp.practice.kits.GameKit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BoxingKit extends BaseDuelKit {
    public BoxingKit() {
        super(GameKit.BOXING);

        setTeams(false);
        setTakeDamage(false);
        setBoxing(true);
        setItemDurability(false);
        setDropItemsOnDeath(false);

        setIcon(new ItemStack(Material.DIAMOND_CHESTPLATE));

        ItemStack[] inv = getItems();

        inv[0] = new ItemStack(Material.DIAMOND_SWORD);
        inv[0].addEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ALL, 1);
    }
}
