package camp.pvp.practice.interactables.impl.queue;

import camp.pvp.practice.guis.queue.DuelQueueGui;
import camp.pvp.practice.guis.queue.MinigameQueueGui;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.queue.GameQueue;
import org.bukkit.entity.Player;

public class PlayInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {

        GameProfile.DefaultQueueMenu defaultQueueMenu = gameProfile.getDefaultQueueMenu();

        switch(defaultQueueMenu) {
            case DUEL_UNRANKED -> new DuelQueueGui(GameQueue.Type.UNRANKED, gameProfile).open(player);
            case DUEL_RANKED -> new DuelQueueGui(GameQueue.Type.RANKED, gameProfile).open(player);
            case MINIGAME -> new MinigameQueueGui(gameProfile).open(player);
            case LAST -> {
                switch(gameProfile.getLastSelectedQueueMenu()) {
                    case DUEL_RANKED -> new DuelQueueGui(GameQueue.Type.RANKED, gameProfile).open(player);
                    case MINIGAME -> new MinigameQueueGui(gameProfile).open(player);
                    default -> new DuelQueueGui(GameQueue.Type.UNRANKED, gameProfile).open(player);
                }
            }
        }
    }
}
