package camp.pvp.practice.kits.impl;

import camp.pvp.practice.kits.BaseDuelKit;
import camp.pvp.practice.kits.GameKit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class ArcherKit extends BaseDuelKit {

    public ArcherKit() {
        super(GameKit.ARCHER);

        setShowHealthBar(true);
        setFfa(true);
        setHunger(false);

        setIcon(new ItemStack(Material.BOW));

        ItemStack[] armor = getArmor(), inv = getItems();

        armor[3] = new ItemStack(Material.LEATHER_HELMET);
        armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
        armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        armor[0] = new ItemStack(Material.LEATHER_BOOTS);

        inv[0] = new ItemStack(Material.BOW);
        inv[0].addEnchantment(Enchantment.ARROW_INFINITE, 1);

        Potion nightVision = new Potion(PotionType.NIGHT_VISION, 1);
        nightVision.setHasExtendedDuration(true);
        inv[1] = nightVision.toItemStack(1);

        inv[9] = new ItemStack(Material.ARROW);
    }
}
