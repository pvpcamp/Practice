package camp.pvp.interactables;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter @Setter
public class InteractableItem {
    private final ItemStack item;
    private final int slot;
    private final ItemInteract interact;
    private String permission;

    public InteractableItem(ItemStack item, int slot, ItemInteract interact) {
        this.item = item;
        this.slot = slot;
        this.interact = interact;
    }
}
