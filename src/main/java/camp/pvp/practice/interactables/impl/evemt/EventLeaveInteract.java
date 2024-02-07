package camp.pvp.practice.interactables.impl.evemt;

import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class EventLeaveInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        gameProfile.getSumoEvent().leave(player);
    }
}
