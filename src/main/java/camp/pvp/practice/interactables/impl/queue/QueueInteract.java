package camp.pvp.practice.interactables.impl.queue;

import camp.pvp.practice.guis.queue.PlayGui;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class QueueInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        new PlayGui(gameProfile).open(player);
    }
}
