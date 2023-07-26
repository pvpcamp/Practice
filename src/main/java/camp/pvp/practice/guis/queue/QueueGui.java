package camp.pvp.practice.guis.queue;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.queue.GameQueueManager;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.entity.Player;

public class QueueGui extends StandardGui {
    public QueueGui(GameQueue.Type queueType, GameProfile profile) {
        super("&6&l" + queueType.toString() + " Queue", 27);

        GameQueueManager gqm = Practice.instance.getGameQueueManager();
        this.setAutoUpdate(true);

        this.setDefaultBackground();

        for(GameQueue queue : gqm.getGameQueues()) {
            if(queue.getType().equals(queueType)) {
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

                if(queueType.equals(GameQueue.Type.RANKED)) {
                    button.setSlot(kit.getRankedSlot());
                } else {
                    button.setSlot(kit.getUnrankedSlot());
                }
                this.addButton(button, false);
            }
        }
    }
}
