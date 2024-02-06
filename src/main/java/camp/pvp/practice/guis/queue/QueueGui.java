package camp.pvp.practice.guis.queue;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.leaderboard.LeaderboardEntry;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.queue.GameQueueManager;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;

import java.util.*;

public class QueueGui extends ArrangedGui {
    public QueueGui(GameQueue.Type queueType, GameProfile profile) {
        super("&6" + queueType.toString() + " Queue");

        GameQueueManager gqm = Practice.getInstance().getGameQueueManager();

        this.setAutoUpdate(true);
        this.setDefaultBackground();

        String buttonName = queueType.equals(GameQueue.Type.RANKED) ? "&aSwitch to &lUnranked Queue" : "&6Switch to &lRanked Queue";
        GuiButton changeQueueType = new GuiButton(
                queueType.equals(GameQueue.Type.RANKED) ? Material.IRON_INGOT : Material.DIAMOND,
                buttonName
        );

        changeQueueType.setOverrideGuiArrangement(true);

        changeQueueType.setAction((p, g) -> {
            GameQueue.Type newQueueType = queueType.equals(GameQueue.Type.RANKED) ? GameQueue.Type.UNRANKED : GameQueue.Type.RANKED;
            profile.setLastSelectedQueueType(newQueueType);
            new QueueGui(newQueueType, profile).open(p);
        });

        changeQueueType.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton b, Gui gui) {

                GameQueue.Type qt = queueType.equals(GameQueue.Type.RANKED) ? GameQueue.Type.UNRANKED : GameQueue.Type.RANKED;

                b.setLore(
                        "&6Playing: &f" + Practice.getInstance().getGameManager().getTotalInGame(qt),
                        "&6In Queue: &f" + gqm.getTotalInQueue(qt),
                        " ",
                        "&7Click to switch to the &f" + qt.toString() + " Queue&7."
                );
            }
        });

        changeQueueType.setSlot(4);
        buttons.add(changeQueueType);

        for(DuelKit kit : DuelKit.values()) {
            GameQueue queue = gqm.getQueue(kit, queueType);
            GuiButton button = new GuiButton(kit.getIcon(), "&6&l" + kit.getDisplayName());

            if(queue == null) continue;
            button.setButtonUpdater(new AbstractButtonUpdater() {
                @Override
                public void update(GuiButton guiButton, Gui gui) {
                    int playing = queue.getPlaying();

                    List<String> lines = new ArrayList<>();

                    lines.add("&6Playing: &f" + playing);
                    lines.add("&6In Queue: &f" + queue.getQueueMembers().size());
                    lines.add(" ");

                    if (queueType.equals(GameQueue.Type.RANKED)) {
                        lines.add("&6Your ELO: &f" + profile.getProfileElo().getRatings().get(kit));
                        lines.add(" ");
                        List<LeaderboardEntry> leaderboardEntries = Practice.getInstance().getGameProfileManager().getLeaderboardUpdater().getLeaderboard().get(kit);
                        for(int i = 0; i < 3; i++) {
                            if(leaderboardEntries.size() < i) break;

                            LeaderboardEntry entry = leaderboardEntries.get(i);

                            StringBuilder sb = new StringBuilder();
                            switch(i) {
                                case 0:
                                    sb.append(" &6&l● #1: &r&6");
                                    break;
                                case 1:
                                    sb.append(" &a● #2: &a");
                                    break;
                                case 2:
                                    sb.append(" &e● #3: &e");
                                    break;
                            }

                            sb.append(entry.getName() + " &7- &f" + entry.getElo());
                            lines.add(sb.toString());
                        }

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

            buttons.add(button);
        }

        setDefaultBorder();
    }
}
