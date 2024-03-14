package camp.pvp.practice.guis.queue;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.minigames.Minigame;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MinigameQueueGui extends ArrangedGui {

    public MinigameQueueGui(GameProfile profile) {
        super("&6Minigame Queue");

        this.setAutoUpdate(true);
        this.setDefaultBorder();

        GuiButton back = new GuiButton(Material.ARROW, "&c&lBack");
        back.setAction((p, b, g, click) -> new PlayGui(profile).open(p));
        back.setLore("&7Click to return to", "&7the play menu.");
        back.setSlot(0);
        back.setOverrideGuiArrangement(true);
        addButton(back);

        for(Minigame.Type minigame : Minigame.Type.values()) {
            GuiButton button = new GuiButton(minigame.getMaterial(), "&6&l" + minigame.toString());
            button.setCloseOnClick(true);
            button.setButtonUpdater((b, g) -> {
                GameQueue queue = Practice.getInstance().getGameQueueManager().getQueue(minigame, GameQueue.Type.UNRANKED);
                int playing = queue.getPlaying();
                List<String> lore = new ArrayList<>(minigame.getDescription());
                lore.add(" ");
                lore.add("&6In Queue: &f" + queue.getQueueMembers().size() + "&7/&f" + minigame.getMaxPlayers());

                if(queue.isCountdown()) {
                    final int time = queue.getTimeBeforeStart();
                    lore.add("&6Starting In: &f" + time + " second" + (time == 1 ? "" : "s"));
                    lore.add(" ");
                }

                lore.add("&6Playing: &f" + playing);
                lore.add(" ");
                lore.add("&7Click to join the queue.");
                b.setLore(lore);

                int stack = playing > 1 ? (Math.min(playing, 64)) : 1;
                b.setAmount(stack);
            });
            button.setAction((p, b, g, click) -> {
                Practice.getInstance().getGameQueueManager().addToQueue(p, minigame, GameQueue.Type.UNRANKED);
            });

            addButton(button);
        }
    }
}
