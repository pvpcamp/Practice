package camp.pvp.practice.interactables;

import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public interface ItemInteract {

    void onInteract(Player player, GameProfile gameProfile);

}
