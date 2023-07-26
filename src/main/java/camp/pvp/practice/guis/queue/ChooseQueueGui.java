package camp.pvp.practice.guis.queue;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.GameManager;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.queue.GameQueueManager;
import camp.pvp.practice.queue.GameQueueMember;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ChooseQueueGui extends StandardGui {
    public ChooseQueueGui(GameProfile profile) {
        super("&6Choose a Queue", 27);

        this.setDefaultBackground();
        this.setAutoUpdate(true);

        GameQueueManager gqm = Practice.instance.getGameQueueManager();
        GameManager gm = Practice.instance.getGameManager();

        GuiButton unranked = new GuiButton(Material.IRON_SWORD, "&a&lUnranked Queue");
        unranked.setButtonUpdater(new AbstractButtonUpdater() {
            int playing = gm.getTotalInGame(GameQueue.Type.UNRANKED);
            int inQueue = gqm.getTotalInQueue(GameQueue.Type.UNRANKED);
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&aPlaying: &f" + playing,
                        "&aIn Queue: &f" + inQueue,
                        " ",
                        "&7Click to view &aUnranked Queues&7.");

                int stack = playing > 1 ? (Math.min(playing, 64)) : 1;
                guiButton.setAmount(stack);
            }
        });
        unranked.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                new QueueGui(GameQueue.Type.UNRANKED, profile).open(player);
            }
        });
        unranked.setSlot(11);
        this.addButton(unranked, false);


        GuiButton ranked = new GuiButton(Material.DIAMOND_SWORD, "&6&lRanked Queue");
        ranked.setButtonUpdater(new AbstractButtonUpdater() {
            int playing = gm.getTotalInGame(GameQueue.Type.RANKED);
            int inQueue = gqm.getTotalInQueue(GameQueue.Type.RANKED);
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&6Playing: &f" + playing,
                        "&6In Queue: &f" + inQueue,
                        " ",
                        "&7Click to view &6Ranked Queues&7.");

                int stack = playing > 1 ? (Math.min(playing, 64)) : 1;
                guiButton.setAmount(stack);
            }
        });
        ranked.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                new QueueGui(GameQueue.Type.RANKED, profile).open(player);
            }
        });
        ranked.setSlot(15);
        this.addButton(ranked, false);
    }
}
