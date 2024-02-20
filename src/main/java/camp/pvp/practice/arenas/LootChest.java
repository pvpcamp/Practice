package camp.pvp.practice.arenas;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.*;

@Data
public class LootChest {

    private Location location;
    private LootChest.Category lootCategory;

    public LootChest(Location location, LootChest.Category lootCategory) {
        this.location = location;
        this.lootCategory = lootCategory;
    }

    public boolean hasItem(ItemStack item) {
        if(getLocation().getBlock() == null || !(getLocation().getBlock() instanceof Chest chest)) return false;

        return chest.getBlockInventory().contains(item);
    }

    public void addItemToChest(ItemStack item, int slot) {
        if(getLocation().getBlock() == null || !(getLocation().getBlock() instanceof Chest chest)) return;

        chest.getBlockInventory().setItem(slot, item);
    }

    public void generateLoot(List<LootChest> nearbyChests) {
        Random random = new Random();
        Set<Integer> usedSlots = new HashSet<>();
        for(LootType type : LootType.values()) {
            List<ItemStack> items = type.getItems(lootCategory);
            Collections.shuffle(items);

            while(!items.isEmpty()) {
                ItemStack i = items.get(0);

                boolean hasItem = false;
                for(LootChest c : nearbyChests) {
                    if(!c.equals(this) && c.getLootCategory().equals(lootCategory) && c.hasItem(i) && c.getLocation().distance(getLocation()) < 20) {
                        items.remove(i);
                        hasItem = true;
                    }
                }

                if(hasItem) continue;

                int slot = random.nextInt(27);
                usedSlots.add(slot);
                while(usedSlots.contains(slot)) {
                    slot = random.nextInt(27);
                    usedSlots.add(slot);
                }

                addItemToChest(i, slot);

                break;
            }
        }
    }

    public String serialize() {
        return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "," + location.getWorld().getName() + "," + lootCategory.name();
    }

    public static LootChest deserialize(String serialized) {
        String[] split = serialized.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        int z = Integer.parseInt(split[2]);
        String world = split[3];
        LootChest.Category category = LootChest.Category.valueOf(split[4]);
        return new LootChest(new Location(Bukkit.getWorld(world), x, y, z), category);
    }

    public enum Category {
        ISLAND, ENHANCED, MIDDLE;
    }

    public enum LootType {
        ARMOR, BLOCKS, FOOD, MELEE, RANGED, RANDOM;

        public ItemStack getRandomItem(Category category) {
            List<ItemStack> items = getItems(category);
            Collections.shuffle(items);
            return items.get(0);
        }

        public List<ItemStack> getItems(Category category) {
            List<ItemStack> items = new ArrayList<>();
            switch(this) {
                case ARMOR:
                    switch (category) {
                        case MIDDLE:
                            ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
                            ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
                            ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
                            ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);

                            helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                            chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                            leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                            boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);

                            items.add(helmet);
                            items.add(chestplate);
                            items.add(leggings);
                            items.add(boots);
                            break;
                        case ENHANCED:
                            items.add(new ItemStack(Material.DIAMOND_CHESTPLATE));
                            items.add(new ItemStack(Material.DIAMOND_LEGGINGS));
                        case ISLAND:
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
                case FOOD:
                    switch (category) {
                        case MIDDLE:
                            items.add(new ItemStack(Material.GOLDEN_APPLE));
                        default:
                            items.add(new ItemStack(Material.COOKED_BEEF, 8));
                            break;
                    }
                    break;
                case MELEE:
                    switch (category) {
                        case MIDDLE:
                            ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                            sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                            items.add(sword);
                        default:
                            items.add(new ItemStack(Material.DIAMOND_SWORD));
                            items.add(new ItemStack(Material.DIAMOND_AXE));
                            break;
                    }
                    break;
                case RANGED:
                    switch (category) {
                        case MIDDLE:
                            ItemStack bow = new ItemStack(Material.BOW);
                            bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
                            items.add(bow);
                        case ENHANCED:
                            items.add(new ItemStack(Material.BOW));
                            items.add(new ItemStack(Material.ARROW, 4));
                        default:
                            items.add(new ItemStack(Material.FISHING_ROD));
                            items.add(new ItemStack(Material.SNOW_BALL, 8));
                            items.add(new ItemStack(Material.EGG, 8));
                            break;
                    }
                    break;
                case RANDOM:
                    switch (category) {
                        case MIDDLE:
                            items.add(new ItemStack(Material.DIAMOND, 4));

                            Potion instantHealth = new Potion(PotionType.INSTANT_HEAL, 2, true);
                            items.add(instantHealth.toItemStack(1));
                        case ENHANCED:
                            items.add(new ItemStack(Material.TNT, 2));
                        default:
                            items.add(new ItemStack(Material.WATER_BUCKET));
                            items.add(new ItemStack(Material.LAVA_BUCKET));

                            Potion splashPoison = new Potion(PotionType.POISON, 1, true);
                            items.add(splashPoison.toItemStack(1));
                            break;
                    }
                    break;
            }

            return items;
        }
    }
}
