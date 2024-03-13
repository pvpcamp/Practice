package camp.pvp.practice.kits.impl;

import camp.pvp.practice.kits.BaseDuelKit;
import camp.pvp.practice.kits.GameKit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ClassicKit extends BaseDuelKit {
    public ClassicKit() {
        super(GameKit.CLASSIC);

        setFfa(true);
        setShowHealthBar(true);

        setIcon(new ItemStack(Material.DIAMOND_SWORD));

        ItemStack[] armor = getArmor(), inv = getItems();

        armor[3] = new ItemStack(Material.DIAMOND_HELMET);
        armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
        armor[0] = new ItemStack(Material.DIAMOND_BOOTS);

        inv[0] = new ItemStack(Material.DIAMOND_SWORD);
        inv[1] = new ItemStack(Material.BOW, 1);
        inv[2] = new ItemStack(Material.FISHING_ROD, 1);
        inv[3] = new ItemStack(Material.GOLDEN_APPLE, 8);

        inv[9] = new ItemStack(Material.ARROW, 12);
    }
}
