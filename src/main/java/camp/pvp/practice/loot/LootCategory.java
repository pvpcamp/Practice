package camp.pvp.practice.loot;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum LootCategory {
    ARMOR, BLOCKS, RANDOM, WEAPONS;

    public int amountForChest(LootChest lootChest) {
        switch(this) {
            case ARMOR:
                switch(lootChest) {
                    case SKYWARS_NORMAL:
                    case SKYWARS_MIDDLE:
                        return 1;
                }
                break;
            case BLOCKS:
                switch(lootChest) {
                    case SKYWARS_MIDDLE:
                    case SKYWARS_NORMAL:
                        return 2;
                }
            case RANDOM:
                switch(lootChest) {
                    case SKYWARS_MIDDLE:
                        return 2;
                    case SKYWARS_NORMAL:
                        return 1;
                }
                break;
            case WEAPONS:
                switch(lootChest) {
                    case SKYWARS_MIDDLE:
                        return 1;
                    case SKYWARS_NORMAL:
                        return 2;
                }
                break;
        }

        return 0;
    }

    public List<ItemStack> getItems(LootChest lootChest) {
        List<ItemStack> items = new ArrayList<>();
        switch(this) {
            case ARMOR:
                switch(lootChest) {
                    case SKYWARS_MIDDLE:
                        items.add(new ItemStack(Material.DIAMOND_CHESTPLATE));
                        items.add(new ItemStack(Material.DIAMOND_LEGGINGS));
                    case SKYWARS_NORMAL:
                        items.add(new ItemStack(Material.DIAMOND_HELMET));
                        items.add(new ItemStack(Material.DIAMOND_BOOTS));
                        break;
                }
                break;
            case BLOCKS:
                items.add(new ItemStack(Material.STONE, 32));
                items.add(new ItemStack(Material.COBBLESTONE, 32));
                items.add(new ItemStack(Material.LOG, 8));
                break;
            case RANDOM:
                switch(lootChest) {
                    case SKYWARS_MIDDLE:
                        Potion speed = new Potion(PotionType.SPEED, 2);
                        speed.setSplash(true);

                        Potion poison = new Potion(PotionType.POISON, 1);
                        poison.setSplash(true);

                        items.add(speed.toItemStack(1));
                        items.add(poison.toItemStack(1));
                        items.add(new ItemStack(Material.GOLDEN_APPLE, 2));
                        items.add(new ItemStack(Material.GOLDEN_APPLE, 1));
                        items.add(new ItemStack(Material.ENDER_PEARL));
                        items.add(new ItemStack(Material.TNT));
                        items.add(new ItemStack(Material.FLINT_AND_STEEL));
                    case SKYWARS_NORMAL:
                        items.add(new ItemStack(Material.COOKED_BEEF, 16));
                        items.add(new ItemStack(Material.EXP_BOTTLE, 8));
                        items.add(new ItemStack(Material.WATER_BUCKET));
                        items.add(new ItemStack(Material.LAVA_BUCKET));
                        items.add(new ItemStack(Material.SNOW_BALL, 8));
                        items.add(new ItemStack(Material.DIAMOND_PICKAXE));
                        items.add(new ItemStack(Material.DIAMOND_AXE));
                        items.add(new ItemStack(Material.DIAMOND_SPADE));
                        break;
                }
                break;
            case WEAPONS:
                switch(lootChest) {
                    case SKYWARS_MIDDLE:
                        ItemStack bow = new ItemStack(Material.BOW);
                        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);

                        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);

                        items.add(bow);
                        items.add(sword);
                        items.add(new ItemStack(Material.ARROW, 16));
                    case SKYWARS_NORMAL:
                        items.add(new ItemStack(Material.DIAMOND_SWORD));
                        items.add(new ItemStack(Material.FISHING_ROD));
                        items.add(new ItemStack(Material.BOW));
                        items.add(new ItemStack(Material.ARROW, 8));
                        break;
                }
        }

        return items;
    }

    public static List<ItemStack> getRandomItems(LootChest lootChest) {
        List<ItemStack> randomItems = new ArrayList<>();
        for(LootCategory category : LootCategory.values()) {
            List<ItemStack> items = category.getItems(lootChest);
            Collections.shuffle(items);
            for(int i = 0; i < category.amountForChest(lootChest); i++) {
                randomItems.add(items.get(i));
            }
        }

        Collections.shuffle(randomItems);
        return randomItems;
    }
}
