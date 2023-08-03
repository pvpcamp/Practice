package camp.pvp.practice.interactables;

import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.inventory.ItemStack;

public interface ItemUpdater {

    void onUpdate(InteractableItem item, GameProfile profile);
}
