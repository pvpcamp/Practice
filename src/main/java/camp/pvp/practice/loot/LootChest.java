package camp.pvp.practice.loot;

import camp.pvp.practice.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public enum LootChest {
    SKYWARS_NORMAL, SKYWARS_MIDDLE;

    public static List<LootChest> getForArenaType(Arena.Type type) {
        switch(type) {
            case DUEL_SKYWARS:
                return Arrays.asList(SKYWARS_MIDDLE, SKYWARS_NORMAL);
            default:
                return Collections.emptyList();
        }
    }

    public int getMaxItems() {
        switch(this) {
            case SKYWARS_NORMAL:
                return 8;
            case SKYWARS_MIDDLE:
                return 5;
            default:
                return 0;
        }
    }

    public int getMinItems() {
        switch(this) {
            case SKYWARS_NORMAL:
                return 4;
            case SKYWARS_MIDDLE:
                return 3;
            default:
                return 0;
        }
    }

    public String getChestName() {
        return this.name();
    }

    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        switch(this) {
            case SKYWARS_NORMAL:
                items.add(new ItemStack(Material.DIAMOND_SWORD));
        }

        return items;
    }

    public void generateLoot(List<Chest> chests) {
        Random random = new Random();
        for(Chest chest : chests) {
            Inventory inventory = chest.getBlockInventory();
            if (inventory.getName() != null && inventory.getName().equals(this.getChestName())) {

                inventory.clear();

                int low = getMinItems();
                int high = getMaxItems();
                int itemCount = random.nextInt(high - low) + low;

                List<ItemStack> originalItems = getItems();
                Collections.shuffle(originalItems);

                Queue<ItemStack> items = new LinkedList<>(originalItems);

                List<Integer> usedSlots = new ArrayList<>();
                for (int i = 0; i < itemCount; i++) {
                    int slot = random.nextInt(27);
                    if (usedSlots.contains(slot)) {
                        while (usedSlots.contains(slot)) {
                            slot = random.nextInt(27);
                        }
                    }

                    usedSlots.add(slot);

                    if(!items.isEmpty()) {
                        inventory.setItem(slot, items.poll());
                    } else {
                        break;
                    }
                }
            }
        }
    }

    public void generateLoot(Location corner1, Location corner2) {
        List<Chest> chests = new ArrayList<>();

        int minX, minY, minZ, maxX, maxY, maxZ;
        minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Location location = new Location(corner1.getWorld(), x, y, z);
                    Block block = location.getBlock();
                    if(!block.isEmpty() && block instanceof Chest) {
                        chests.add((Chest) block);
                    }
                }
            }
        }

        generateLoot(chests);
    }
}
