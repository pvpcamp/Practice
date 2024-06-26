package camp.pvp.practice.kits.impl;

import camp.pvp.practice.kits.BaseDuelKit;
import camp.pvp.practice.kits.GameKit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class InvadedKit extends BaseDuelKit {
    public InvadedKit() {
        super(GameKit.INVADED);

        setFfa(true);
        setShowHealthBar(true);
        setArrowPickup(false);

        setIcon(new ItemStack(Material.BLAZE_POWDER));

        ItemStack[] armor = getArmor(), inv = getItems();

        armor[3] = new ItemStack(Material.DIAMOND_HELMET);
        armor[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        armor[3].addEnchantment(Enchantment.DURABILITY, 3);

        armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        armor[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        armor[2].addEnchantment(Enchantment.DURABILITY, 3);

        armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
        armor[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        armor[1].addEnchantment(Enchantment.DURABILITY, 3);

        armor[0] = new ItemStack(Material.DIAMOND_BOOTS);
        armor[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);
        armor[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        armor[0].addEnchantment(Enchantment.DURABILITY, 3);

        inv[0] = new ItemStack(Material.DIAMOND_SWORD);
        inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 3);
        inv[0].addEnchantment(Enchantment.DURABILITY, 3);

        inv[1] = new ItemStack(Material.BOW, 1);
        inv[1].addEnchantment(Enchantment.ARROW_DAMAGE, 2);
        inv[1].addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
        inv[1].addEnchantment(Enchantment.ARROW_INFINITE, 1);

        inv[2] = new ItemStack(Material.FISHING_ROD, 1);
        inv[3] = new ItemStack(Material.GOLDEN_APPLE, 64);
        inv[4] = new ItemStack(Material.GOLDEN_APPLE, 2);
        inv[4].setDurability((short) 1);

        Potion instantDamage = new Potion(PotionType.INSTANT_DAMAGE, 1);
        instantDamage.setSplash(true);
        inv[5] = instantDamage.toItemStack(1);

        inv[6] = new ItemStack(Material.ENDER_PEARL);

        Potion speed = new Potion(PotionType.SPEED, 1);
        speed.setHasExtendedDuration(true);
        inv[7] = speed.toItemStack(1);

        inv[9] = new ItemStack(Material.ARROW, 1);
    }
}
