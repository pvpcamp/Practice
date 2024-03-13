package camp.pvp.practice.kits.impl;

import camp.pvp.practice.kits.BaseDuelKit;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.utils.Colors;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BuildUHCKit extends BaseDuelKit {
    public BuildUHCKit() {
        super(GameKit.BUILD_UHC);

        setBuild(true);
        setFfa(true);
        setShowHealthBar(true);
        setDropItemsOnDeath(true);

        setIcon(new ItemStack(Material.LAVA_BUCKET));

        ItemStack[] armor = getArmor(), inv = getItems();

        armor[3] = new ItemStack(Material.DIAMOND_HELMET);
        armor[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

        armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        armor[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
        armor[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        armor[0] = new ItemStack(Material.DIAMOND_BOOTS);
        armor[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

        inv[0] = new ItemStack(Material.DIAMOND_SWORD);
        inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 3);
        inv[1] = new ItemStack(Material.FISHING_ROD, 1);
        inv[2] = new ItemStack(Material.BOW);
        inv[2].addEnchantment(Enchantment.ARROW_DAMAGE, 3);
        inv[3] = new ItemStack(Material.COOKED_BEEF, 64);
        inv[4] = new ItemStack(Material.GOLDEN_APPLE, 6);

        inv[5] = new ItemStack(Material.GOLDEN_APPLE, 3);
        ItemMeta headMeta = inv[5].getItemMeta();
        headMeta.setDisplayName(Colors.get("&6Golden Head"));
        inv[5].setItemMeta(headMeta);

        inv[6] = new ItemStack(Material.DIAMOND_PICKAXE);
        inv[7] = new ItemStack(Material.DIAMOND_AXE);
        inv[8] = new ItemStack(Material.WOOD, 64);
        inv[9] = new ItemStack(Material.ARROW, 20);
        inv[10] = new ItemStack(Material.COBBLESTONE, 64);
        inv[11] = new ItemStack(Material.WATER_BUCKET);
        inv[12] = new ItemStack(Material.WATER_BUCKET);
        inv[13] = new ItemStack(Material.LAVA_BUCKET);
        inv[14] = new ItemStack(Material.LAVA_BUCKET);
    }
}
