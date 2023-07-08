package camp.pvp.interactables;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class InteractableItem {
    @Getter private final ItemStack item;
    @Getter private final int slot;
    @Getter private final ItemInteract interact;

    public InteractableItem(ItemStack item, int slot, ItemInteract interact) {
        this.item = item;
        this.slot = slot;
        this.interact = interact;
    }
}
