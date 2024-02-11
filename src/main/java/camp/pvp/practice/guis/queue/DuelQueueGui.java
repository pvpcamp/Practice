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
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public class DuelQueueGui extends ArrangedGui {
    public DuelQueueGui(GameQueue.Type queueType, GameProfile profile) {
        super("&6" + queueType.toString() + " Queue");

        GameQueueManager gqm = Practice.getInstance().getGameQueueManager();

        this.setAutoUpdate(true);
        this.setDefaultBackground();

        GuiButton unrankedQueue = new GuiButton(Material.IRON_SWORD, "&a&lUnranked Queue");

        if(queueType.equals(GameQueue.Type.UNRANKED)) {
            unrankedQueue.addEnchantment(Enchantment.DURABILITY, 1);
        }

        unrankedQueue.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                if(queueType.equals(GameQueue.Type.UNRANKED)) {
                    guiButton.setLore(
                            "&ePlaying: &f" + Practice.getInstance().getGameManager().getTotalInGame(GameQueue.Type.UNRANKED),
                            "&eIn Queue: &f" + gqm.getTotalInQueue(GameQueue.Type.UNRANKED)
                            );
                } else {
                    guiButton.setLore(
                            "&6Playing: &f" + Practice.getInstance().getGameManager().getTotalInGame(GameQueue.Type.UNRANKED),
                            "&6In Queue: &f" + gqm.getTotalInQueue(GameQueue.Type.UNRANKED),
                            " ",
                            "&7Click to view &aUnranked Queues&7."
                    );
                }
            }
        });

        unrankedQueue.setAction((p, b, g, click) -> {
            if(queueType.equals(GameQueue.Type.RANKED)) {
                profile.setLastSelectedQueueType(GameQueue.Type.UNRANKED);
                new DuelQueueGui(GameQueue.Type.UNRANKED, profile).open(p);
            }
        });
        unrankedQueue.setOverrideGuiArrangement(true);
        unrankedQueue.setSlot(2);
        addButton(unrankedQueue);

        GuiButton rankedQueue = new GuiButton(Material.DIAMOND_SWORD, "&6&lRanked Queue");

        if(queueType.equals(GameQueue.Type.RANKED)) {
            rankedQueue.addEnchantment(Enchantment.DURABILITY, 1);
        }

        rankedQueue.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                if(queueType.equals(GameQueue.Type.RANKED)) {
                    guiButton.setLore(
                            "&ePlaying: &f" + Practice.getInstance().getGameManager().getTotalInGame(GameQueue.Type.RANKED),
                            "&eIn Queue: &f" + gqm.getTotalInQueue(GameQueue.Type.RANKED)
                    );
                } else {
                    guiButton.setLore(
                            "&6Playing: &f" + Practice.getInstance().getGameManager().getTotalInGame(GameQueue.Type.RANKED),
                            "&6In Queue: &f" + gqm.getTotalInQueue(GameQueue.Type.RANKED),
                            " ",
                            "&7Click to view &6Ranked Queues&7."
                    );
                }
            }
        });

        rankedQueue.setAction((p, b, g, click) -> {
            if(queueType.equals(GameQueue.Type.UNRANKED)) {
                profile.setLastSelectedQueueType(GameQueue.Type.RANKED);
                new DuelQueueGui(GameQueue.Type.RANKED, profile).open(p);
            }
        });
        rankedQueue.setOverrideGuiArrangement(true);
        rankedQueue.setSlot(6);
        addButton(rankedQueue);

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

            button.setAction((p, b, g, clickType) -> {
                gqm.addToQueue(p, queue);
            });
            button.setCloseOnClick(true);

            buttons.add(button);
        }

        setDefaultBorder();
    }
}
