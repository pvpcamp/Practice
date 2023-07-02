package camp.pvp.interactables;

import camp.pvp.profiles.GameProfile;
import org.bukkit.entity.Player;

public interface ItemInteract {

    void onInteract(Player player, GameProfile gameProfile);

}
