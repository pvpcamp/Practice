package camp.pvp.practice.kits.impl;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.kits.BaseDuelKit;
import camp.pvp.practice.kits.BaseKit;
import camp.pvp.practice.kits.NewGameKit;
import camp.pvp.practice.queue.GameQueue;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class NoDebuffKit extends BaseDuelKit {

    public NoDebuffKit() {
        super(NewGameKit.NO_DEBUFF);

        Potion potion = new Potion(PotionType.INSTANT_HEAL, 2);
        potion.setSplash(true);

        setIcon(potion.toItemStack(1));
        setFfa(true);

        ItemStack[] armor = getArmor(), inv = getItems(), more = getMoreItems();

        for (int i = 0; i < 36; i++) {
            more[i] = getItems()[i].clone();
        }

        more[7] = new ItemStack(Material.GOLDEN_CARROT, 64);

        armor[3] = new ItemStack(Material.DIAMOND_HELMET);
        armor[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        armor[3].addEnchantment(Enchantment.DURABILITY, 3);

        armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        armor[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        armor[2].addEnchantment(Enchantment.DURABILITY, 3);

        armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
        armor[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        armor[1].addEnchantment(Enchantment.DURABILITY, 3);

        armor[0] = new ItemStack(Material.DIAMOND_BOOTS);
        armor[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);
        armor[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        armor[0].addEnchantment(Enchantment.DURABILITY, 3);

        inv[0] = new ItemStack(Material.DIAMOND_SWORD);
        inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 3);
        inv[0].addEnchantment(Enchantment.FIRE_ASPECT, 2);
        inv[0].addEnchantment(Enchantment.DURABILITY, 3);

        inv[1] = new ItemStack(Material.ENDER_PEARL, 16);
        inv[8] = new ItemStack(Material.COOKED_BEEF, 64);

        Potion speed = new Potion(PotionType.SPEED, 2);

        Potion fireResistance = new Potion(PotionType.FIRE_RESISTANCE, 1);
        fireResistance.setHasExtendedDuration(true);

        Potion health = new Potion(PotionType.INSTANT_HEAL, 2);
        health.setSplash(true);

        inv[2] = speed.toItemStack(1);
        inv[17] = speed.toItemStack(1);
        inv[26] = speed.toItemStack(1);
        inv[35] = speed.toItemStack(1);

        inv[3] = fireResistance.toItemStack(1);

        for(int x = 0; x < 36; x++) {
            ItemStack i = inv[x];
            if(i == null) {
                inv[x] = health.toItemStack(1);
            }
        }
    }
}
