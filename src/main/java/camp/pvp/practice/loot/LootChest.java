package camp.pvp.practice.loot;

import camp.pvp.practice.arenas.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;

public enum LootChest {
    SKYWARS_NORMAL, SKYWARS_MIDDLE;

    public static List<LootChest> getForArenaType(Arena.Type type) {
        switch (type) {
            case DUEL_SKYWARS:
                return Arrays.asList(SKYWARS_MIDDLE, SKYWARS_NORMAL);
            default:
                return Collections.emptyList();
        }
    }

    public static void generateLoot(Arena arena) {
        for (LootChest lc : getForArenaType(arena.getType())) {
            Random random = new Random();
            for (Location location : arena.getChests()) {

                Block block = location.getBlock();

                boolean valid = lc.isTrappedChest() ? block.getType().equals(Material.TRAPPED_CHEST) : block.getType().equals(Material.CHEST);

                if (valid) {
                    block.getState().update(true, false);
                    BlockState blockState = block.getState();
                    if (blockState instanceof Chest) {
                        Chest chest = (Chest) blockState;

                        Inventory inventory = chest.getBlockInventory();
                        inventory.clear();

                        List<ItemStack> originalItems = LootCategory.getRandomItems(lc);

                        Queue<ItemStack> items = new LinkedList<>(originalItems);

                        List<Integer> usedSlots = new ArrayList<>();
                        while (!items.isEmpty()) {
                            int slot = random.nextInt(27);
                            if (usedSlots.contains(slot)) {
                                while (usedSlots.contains(slot)) {
                                    slot = random.nextInt(27);
                                }
                            }

                            usedSlots.add(slot);
                            inventory.setItem(slot, items.poll());
                        }
                    }
                }
            }
        }
    }

    public boolean isTrappedChest() {
        switch(this) {
            case SKYWARS_MIDDLE:
                return true;
            default:
                return false;
        }
    }
}
