package camp.pvp.practice.interactables;

import camp.pvp.practice.utils.Colors;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter @Setter
public class InteractableItem {
    private final ItemStack item;
    private final int slot;
    private final ItemInteract interact;
    private String permission;
    private ItemUpdater itemUpdater;

    public InteractableItem(ItemStack item, int slot, ItemInteract interact) {
        this.item = item;
        this.slot = slot;
        this.interact = interact;
    }

    public InteractableItem(ItemStack item, int slot, ItemInteract interact, ItemUpdater itemUpdater) {
        this.item = item;
        this.slot = slot;
        this.interact = interact;
        this.itemUpdater = itemUpdater;
    }

    public void updateName(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Colors.get(name));
        item.setItemMeta(meta);
    }
}
