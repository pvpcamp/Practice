package camp.pvp.interactables;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class InteractableItem {
    @Getter private final ItemStack item;
    @Getter private final int slot;

    public InteractableItem(ItemStack item, int slot) {
        this.item = item;
        this.slot = slot;
    }
}
