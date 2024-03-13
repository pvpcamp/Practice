package camp.pvp.practice.kits.impl;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.kits.BaseDuelKit;
import camp.pvp.practice.kits.GameKit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.Collections;

public class StrategyKit extends BaseDuelKit {
    public StrategyKit() {
        super(GameKit.STRATEGY);

        setBuild(true);
        setShowHealthBar(true);

        setIcon(new ItemStack(Material.WEB));

        setArenaTypes(Collections.singletonList(Arena.Type.DUEL_BUILD));

        ItemStack[] armor = getArmor(), inv = getItems();

        armor[3] = new ItemStack(Material.DIAMOND_HELMET);
        armor[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        armor[3].addEnchantment(Enchantment.DURABILITY, 3);

        armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        armor[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        armor[2].addEnchantment(Enchantment.DURABILITY, 3);

        armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
        armor[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        armor[1].addEnchantment(Enchantment.DURABILITY, 3);

        armor[0] = new ItemStack(Material.DIAMOND_BOOTS);
        armor[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        armor[0].addEnchantment(Enchantment.DURABILITY, 3);

        inv[0] = new ItemStack(Material.DIAMOND_AXE);
        inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 5);
        inv[0].addEnchantment(Enchantment.DURABILITY, 3);

        inv[1] = new ItemStack(Material.WEB, 16);
        inv[2] = new ItemStack(Material.GOLDEN_APPLE, 16);
        inv[3] = new ItemStack(Material.WOOD, 64);
        inv[4] = new ItemStack(Material.LAVA_BUCKET);
        inv[5] = new ItemStack(Material.WATER_BUCKET);

        inv[6] = new ItemStack(Material.BOW);
        inv[6].addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
        inv[6].addEnchantment(Enchantment.ARROW_INFINITE, 1);
        inv[6].addEnchantment(Enchantment.DURABILITY, 3);

        inv[9] = new ItemStack(Material.ARROW, 1);
        inv[10] = new ItemStack(Material.ENDER_PEARL, 3);
        inv[11] = new ItemStack(Material.SNOW_BALL, 16);
        inv[12] = new ItemStack(Material.EGG, 16);

        ItemStack milk = new ItemStack(Material.MILK_BUCKET);
        inv[13] = milk;
        inv[14] = milk.clone();

        inv[15] = new ItemStack(Material.COOKED_BEEF, 64);

        Potion speed = new Potion(PotionType.SPEED, 2);

        inv[8] = speed.toItemStack(1);
        inv[17] = speed.toItemStack(1);
        inv[26] = speed.toItemStack(1);
        inv[35] = speed.toItemStack(1);

        Potion strength = new Potion(PotionType.STRENGTH, 1);
        strength.setHasExtendedDuration(true);
        inv[7] = strength.toItemStack(1);
        inv[16] = strength.toItemStack(1);

        Potion poison = new Potion(PotionType.POISON, 1);
        poison.setSplash(true);

        inv[27] = poison.toItemStack(1);
        inv[28] = poison.toItemStack(1);

        Potion damage = new Potion(PotionType.INSTANT_DAMAGE, 2);
        damage.setSplash(true);
        inv[29] = damage.toItemStack(1);
        inv[30] = damage.toItemStack(1);

        Potion health = new Potion(PotionType.INSTANT_HEAL, 2);
        health.setSplash(true);
        inv[31] = health.toItemStack(1);
        inv[32] = health.toItemStack(1);
    }
}
