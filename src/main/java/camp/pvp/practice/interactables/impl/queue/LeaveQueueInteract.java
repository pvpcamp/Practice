package camp.pvp.practice.interactables.impl.queue;

import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import org.bukkit.entity.Player;

public class LeaveQueueInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Practice.getInstance().getGameQueueManager().removeFromQueue(player);
    }
}
