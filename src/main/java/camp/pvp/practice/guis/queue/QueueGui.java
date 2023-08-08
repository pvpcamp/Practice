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

import java.util.ArrayList;
import java.util.List;

public class QueueGui extends StandardGui {
    public QueueGui(GameQueue.Type queueType, GameProfile profile) {
        super("&6&l" + queueType.toString() + " Queue", 36);

        GameQueueManager gqm = Practice.instance.getGameQueueManager();
        this.setAutoUpdate(true);

        this.setDefaultBackground();

        for(GameQueue queue : gqm.getGameQueues()) {
            if(queue.getType().equals(queueType)) {
                DuelKit kit = queue.getDuelKit();
                GuiButton button = new GuiButton(kit.getIcon(), "&6" + kit.getDisplayName());
                button.setButtonUpdater(new AbstractButtonUpdater() {
                    @Override
                    public void update(GuiButton guiButton, Gui gui) {
                        int playing = queue.getPlaying();

                        List<String> lines = new ArrayList<>();

                        lines.add("&6Playing: &f" + playing);
                        lines.add("&6In Queue: &f" + queue.getQueueMembers().size());
                        lines.add(" ");

                        if(queueType.equals(GameQueue.Type.RANKED)) {
                            lines.add("&6Your ELO: &f" + profile.getProfileElo().getRatings().get(kit));
                            lines.add(" ");
                        }

                        lines.add("&7Click to join the &f" + queueType.toString() + " " + kit.getDisplayName() + " &7queue.");

                        guiButton.setLore(lines);

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
