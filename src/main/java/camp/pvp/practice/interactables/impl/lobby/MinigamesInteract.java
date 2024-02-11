package camp.pvp.practice.interactables.impl.lobby;

import camp.pvp.practice.Practice;
import camp.pvp.practice.guis.queue.MinigameQueueGui;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class MinigamesInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        new MinigameQueueGui(gameProfile).open(player);
    }
}
