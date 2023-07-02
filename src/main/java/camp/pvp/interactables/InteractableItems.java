package camp.pvp.interactables;

import camp.pvp.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public enum InteractableItems {
    QUEUE, PIT, CREATE_PARTY, KIT_EDITOR, SETTINGS,
    STOP_SPECTATING, SHOW_PLAYERS,
    LEAVE_QUEUE;

    public InteractableItem getItem() {
        switch(this) {
            // LOBBY
            case QUEUE:
                return new InteractableItem(
                        new ItemBuilder(Material.DIAMOND_SWORD, "&6Join a Queue").create(),
                        0
                );
            case PIT:
                return new InteractableItem(
                        new ItemBuilder(Material.DIAMOND_SWORD, "&6Join a Queue").create(),
                        1
                );
            case CREATE_PARTY:
                return new InteractableItem(
                        new ItemBuilder(Material.DIAMOND_SWORD, "&6Create Party").create(),
                        4
                );
            case KIT_EDITOR:
                return new InteractableItem(
                        new ItemBuilder(Material.DIAMOND_SWORD, "&6Join a Queue").create(),
                        7
                );
            case SETTINGS:
                return new InteractableItem(
                        new ItemBuilder(Material.DIAMOND_SWORD, "&6Join a Queue").create(),
                        8
                );
            // LOBBY_QUEUE
            default:
                return null;
        }
    }
}
