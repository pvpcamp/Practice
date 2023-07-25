package camp.pvp.practice.interactables.impl.queue;

import camp.pvp.practice.guis.queue.UnrankedQueueGui;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.queue.GameQueueManager;
import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.entity.Player;

public class QueueInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        new UnrankedQueueGui().open(player);
    }
}
