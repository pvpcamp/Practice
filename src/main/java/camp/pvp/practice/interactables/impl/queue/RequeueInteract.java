package camp.pvp.practice.interactables.impl.queue;

import camp.pvp.practice.Practice;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.PreviousQueue;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.queue.GameQueueManager;
import org.bukkit.entity.Player;

public class RequeueInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        PreviousQueue previousQueue = gameProfile.getPreviousQueue();
        GameQueueManager gqm = Practice.getInstance().getGameQueueManager();

        for(GameQueue queue : gqm.getGameQueues()) {
            if(queue.getType().equals(previousQueue.getQueueType()) && queue.getDuelKit().equals(previousQueue.getKit())) {
                gqm.addToQueue(player, queue);
                return;
            }
        }
    }
}
