package camp.pvp.practice.kits.impl;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.kits.BaseDuelKit;
import camp.pvp.practice.kits.GameKit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class FireballFightKit extends BaseDuelKit {

    public FireballFightKit() {
        super(GameKit.FIREBALL_FIGHT);

        setBuild(true);
        setBedwars(true);
        setApplyLeatherTeamColor(true);
        setHunger(false);
        setMoveOnStart(false);
        setItemDurability(false);
        setRespawn(true);
        setPlaceTntBeforeStart(true);
        setBiggerExplosions(true);
        setFallDamage(false);
        setDropItemsOnDeath(false);
        setShowHealthBar(true);

        setArenaTypes(Collections.singletonList(Arena.Type.DUEL_FIREBALL_FIGHT));

        setIcon(new ItemStack(Material.FIREBALL));

        ItemStack[] armor = getArmor(), inv = getItems();

        armor[3] = new ItemStack(Material.LEATHER_HELMET);
        armor[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        armor[3].addEnchantment(Enchantment.DURABILITY, 3);

        armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
        armor[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        armor[2].addEnchantment(Enchantment.DURABILITY, 3);

        armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        armor[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        armor[1].addEnchantment(Enchantment.DURABILITY, 3);

        armor[0] = new ItemStack(Material.LEATHER_BOOTS);
        armor[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        armor[0].addEnchantment(Enchantment.DURABILITY, 3);

        inv[0] = new ItemStack(Material.STONE_SWORD);
        inv[1] = new ItemStack(Material.WOOL, 64);
        inv[2] = new ItemStack(Material.ENDER_STONE, 8);
        inv[3] = new ItemStack(Material.FIREBALL, 6);

        ItemMeta fireballMeta = inv[3].getItemMeta();
        fireballMeta.setDisplayName(ChatColor.GOLD + "Fireball");
        inv[3].setItemMeta(fireballMeta);

        inv[4] = new ItemStack(Material.WOOD_PICKAXE);
        inv[4].addEnchantment(Enchantment.DIG_SPEED, 1);

        inv[5] = new ItemStack(Material.WOOD_AXE);
        inv[5].addEnchantment(Enchantment.DIG_SPEED, 1);

        inv[6] = new ItemStack(Material.SHEARS);
        inv[7] = new ItemStack(Material.TNT, 2);
        inv[8] = new ItemStack(Material.LADDER, 8);
    }
}
