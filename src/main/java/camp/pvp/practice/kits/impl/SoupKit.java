package camp.pvp.practice.kits.impl;

import camp.pvp.practice.kits.BaseDuelKit;
import camp.pvp.practice.kits.GameKit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SoupKit extends BaseDuelKit {
    public SoupKit() {
        super(GameKit.SOUP);

        setShowHealthBar(true);
        setHunger(false);
        setFfa(true);

        setIcon(new ItemStack(Material.MUSHROOM_SOUP));

        ItemStack[] armor = getArmor(), inv = getItems();

        armor[3] = new ItemStack(Material.IRON_HELMET);
        armor[2] = new ItemStack(Material.IRON_CHESTPLATE);
        armor[1] = new ItemStack(Material.IRON_LEGGINGS);
        armor[0] = new ItemStack(Material.IRON_BOOTS);

        inv[0] = new ItemStack(Material.DIAMOND_SWORD);

        for(int x = 0; x < 36; x++) {
            ItemStack i = inv[x];
            if(i == null) {
                inv[x] = new ItemStack(Material.MUSHROOM_SOUP);
            }
        }
    }
}
