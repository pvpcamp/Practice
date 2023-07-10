package camp.pvp.interactables.impl.queue;

import camp.pvp.Practice;
import camp.pvp.interactables.ItemInteract;
import camp.pvp.profiles.GameProfile;
import org.bukkit.entity.Player;

public class LeaveQueueInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Practice.instance.getGameQueueManager().removeFromQueue(player);
    }
}
