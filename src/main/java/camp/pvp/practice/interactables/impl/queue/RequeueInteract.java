package camp.pvp.practice.interactables.impl.queue;

import camp.pvp.practice.Practice;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.PreviousQueue;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.queue.GameQueueManager;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class RequeueInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        PreviousQueue previousQueue = gameProfile.getPreviousQueue();
        GameQueueManager gqm = Practice.getInstance().getGameQueueManager();

        GameQueue queue = null;

        for(GameQueue q : gqm.getGameQueues()) {
            if(q.getType().equals(previousQueue.getQueueType()) && q.getDuelKit().equals(previousQueue.getKit())) {
                queue = q;
                break;
            }
        }

        if(queue == null) {
            player.sendMessage(ChatColor.RED + "The queue you were attempting to requeue for is no longer available.");
            gameProfile.setPreviousQueue(null);
            gameProfile.givePlayerItems(false);
            return;
        }

        if(queue.getType().equals(GameQueue.Type.RANKED)) {
            StandardGui confirm = new StandardGui("&6Confirm Requeue", 9);

            for(int i = 0; i < 9; i++) {
                GuiButton button = new GuiButton(Material.DIAMOND_SWORD, "&6&lAre you sure?");
                button.setCloseOnClick(true);
                button.setLore(
                        "&7You are attempting to requeue",
                        "&7for a ranked match.",
                        " ",
                        "&6Kit: &f" + queue.getDuelKit().getDisplayName(),
                        " ",
                        "&7Click to confirm requeue.");
                button.setSlot(i);
                GameQueue finalQueue = queue;
                button.setAction(new GuiAction() {
                    @Override
                    public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                        gqm.addToQueue(player, finalQueue);
                    }
                });
                confirm.addButton(button);
                confirm.open(player);
            }
        } else {
            gqm.addToQueue(player, queue);
        }
    }
}
