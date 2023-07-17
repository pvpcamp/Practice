package camp.pvp.practice.interactables.impl.queue;

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
        GameQueueManager gqm = Practice.instance.getGameQueueManager();
        StandardGui gui = new StandardGui("&6Unranked Queue", 27);
        gui.setAutoUpdate(true);

        gui.setDefaultBackground();

        for(GameQueue queue : gqm.getGameQueues()) {
            DuelKit kit = queue.getDuelKit();
            GuiButton button = new GuiButton(kit.getIcon(), kit.getColor() + kit.getDisplayName());
            button.setButtonUpdater(new AbstractButtonUpdater() {
                @Override
                public void update(GuiButton guiButton, Gui gui) {
                    int playing = queue.getPlaying();
                    guiButton.setLore(
                            "&6Playing: &f" + playing,
                            "&6In Queue: &f" + queue.getQueueMembers().size(),
                            "&7Click to join queue!");

                    int stack = playing > 1 ? (Math.min(playing, 64)) : 1;
                    guiButton.setAmount(stack);
                }
            });

            button.setAction((p, g) -> {
                gqm.addToQueue(p, queue);
            });
            button.setCloseOnClick(true);

            button.setSlot(kit.getUnrankedSlot());
            gui.addButton(button, false);
        }

        gui.open(player);
    }
}
