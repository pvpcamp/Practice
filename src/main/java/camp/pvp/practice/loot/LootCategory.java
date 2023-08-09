package camp.pvp.practice.loot;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum LootCategory {
    ARMOR, FOOD, WEAPONS;

    public int amountForChest(LootChest lootChest) {
        switch(this) {
            case ARMOR:
                switch(lootChest) {
                    case SKYWARS_MIDDLE:
                        return 1;
                    case SKYWARS_NORMAL:
                        return 2;
                }
                break;
            case FOOD:
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
        switch(this) {
            case ARMOR:
                switch(lootChest) {

                }
        }
    }

    public static List<ItemStack> getRandomItems(LootChest lootChest) {
        List<ItemStack> randomItems = new ArrayList<>();
        for(LootCategory category : LootCategory.values()) {
            List<ItemStack> items = category.getItems(lootChest);
            Collections.shuffle(items);
            for(int i = 0; i < category.amountForChest(lootChest); i++) {
                randomItems.add(items.get(i));
                items.remove(i);
            }
        }

        Collections.shuffle(randomItems);
        return randomItems;
    }
}
