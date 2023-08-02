package camp.pvp.practice.interactables.impl.queue;

import camp.pvp.practice.Practice;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class RequeueInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Practice.instance.getGameQueueManager().addToQueue(player, gameProfile.getPreviousQueue());
    }
}
