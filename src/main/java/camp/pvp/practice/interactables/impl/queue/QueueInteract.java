package camp.pvp.practice.interactables.impl.queue;

import camp.pvp.practice.guis.queue.DuelQueueGui;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class QueueInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        new DuelQueueGui(gameProfile.getLastSelectedQueueType(), gameProfile).open(player);
    }
}
