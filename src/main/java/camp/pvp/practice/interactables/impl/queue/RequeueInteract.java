package camp.pvp.practice.interactables.impl.queue;

import camp.pvp.practice.Practice;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.PreviousQueue;
import camp.pvp.practice.queue.GameQueue;
import org.bukkit.entity.Player;

public class RequeueInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        PreviousQueue previousQueue = gameProfile.getPreviousQueue();

        for(GameQueue queue : Practice.instance.getGameQueueManager().getGameQueues()) {
            if(queue.getType().equals(previousQueue.getQueueType()) && queue.getDuelKit().equals(previousQueue.getKit())) {
                Practice.instance.getGameQueueManager().addToQueue(player, queue);
                return;
            }
        }
    }
}
