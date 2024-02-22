package camp.pvp.practice.guis.queue;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.leaderboard.LeaderboardEntry;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.queue.GameQueueManager;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import camp.pvp.utils.guis.Gui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public class DuelQueueGui extends ArrangedGui {
    public DuelQueueGui(GameQueue.Type queueType, GameProfile profile) {
        super("&6" + queueType.toString() + " Queue");

        GameQueueManager gqm = Practice.getInstance().getGameQueueManager();

        setAutoUpdate(true);
        setDefaultNavigationBar();

        GuiButton back = new GuiButton(Material.ARROW, "&c&lBack");
        back.setAction((p, b, g, click) -> new ChooseQueueGui(profile).open(p));
        back.setLore("&7Click to return to", "&7the play menu.");
        back.setSlot(0);
        back.setOverrideGuiArrangement(true);
        addButton(back);

        final ChatColor color = queueType.getColor();

        GuiButton unrankedQueue = new GuiButton(Material.IRON_SWORD, GameQueue.Type.UNRANKED.getColor() + "&lUnranked Queue");

        if(queueType.equals(GameQueue.Type.UNRANKED)) {
            unrankedQueue.addEnchantment(Enchantment.DURABILITY, 1);
        }

        unrankedQueue.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                if(queueType.equals(GameQueue.Type.UNRANKED)) {
                    guiButton.setLore(
                            GameQueue.Type.UNRANKED.getColor() + "Playing: &f" + Practice.getInstance().getGameManager().getTotalInGame(GameQueue.Type.UNRANKED),
                            GameQueue.Type.UNRANKED.getColor() + "In Queue: &f" + gqm.getTotalInQueue(GameQueue.Type.UNRANKED)
                            );
                } else {
                    guiButton.setLore(
                            GameQueue.Type.UNRANKED.getColor() + "Playing: &f" + Practice.getInstance().getGameManager().getTotalInGame(GameQueue.Type.UNRANKED),
                            GameQueue.Type.UNRANKED.getColor() + "In Queue: &f" + gqm.getTotalInQueue(GameQueue.Type.UNRANKED),
                            " ",
                            "&7Click to view" + GameQueue.Type.UNRANKED.getColor() + " Unranked Queues&7."
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
        unrankedQueue.setSlot(3);
        addButton(unrankedQueue);

        GuiButton rankedQueue = new GuiButton(Material.DIAMOND_SWORD, GameQueue.Type.RANKED.getColor() + "&lRanked Queue");

        if(queueType.equals(GameQueue.Type.RANKED)) {
            rankedQueue.addEnchantment(Enchantment.DURABILITY, 1);
        }

        rankedQueue.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                if(queueType.equals(GameQueue.Type.RANKED)) {
                    guiButton.setLore(
                            GameQueue.Type.RANKED.getColor() + "Playing: &f" + Practice.getInstance().getGameManager().getTotalInGame(GameQueue.Type.RANKED),
                            GameQueue.Type.RANKED.getColor() + "In Queue: &f" + gqm.getTotalInQueue(GameQueue.Type.RANKED)
                    );
                } else {
                    guiButton.setLore(
                            GameQueue.Type.RANKED.getColor() + "Playing: &f" + Practice.getInstance().getGameManager().getTotalInGame(GameQueue.Type.RANKED),
                            GameQueue.Type.RANKED.getColor() + "In Queue: &f" + gqm.getTotalInQueue(GameQueue.Type.RANKED),
                            " ",
                            "&7Click to view " + GameQueue.Type.RANKED.getColor() + "Ranked Queues&7."
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
        rankedQueue.setSlot(5);
        addButton(rankedQueue);

        for(GameKit kit : GameKit.values()) {
            GameQueue queue = gqm.getQueue(kit, queueType);
            GuiButton button = new GuiButton(kit.getIcon(), color + "&l" + kit.getDisplayName());

            if(queue == null) continue;

            button.setButtonUpdater(new AbstractButtonUpdater() {
                @Override
                public void update(GuiButton guiButton, Gui gui) {
                    int playing = queue.getPlaying();

                    List<String> lines = new ArrayList<>();

                    lines.add(color + "Playing: &f" + playing);
                    lines.add(color + "In Queue: &f" + queue.getQueueMembers().size());
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

                    if(queue.isAvailable()) {
                        lines.add("&7Click to join the");
                        lines.add(color + queueType.toString() + " " + kit.getDisplayName() + " &7queue.");
                    } else {
                        lines.add("&c&lThis queue is disabled.");
                    }

                    guiButton.setLore(lines);

                    int stack = playing > 1 ? (Math.min(playing, 64)) : 1;
                    guiButton.setAmount(stack);
                }
            });

            button.setAction((p, b, g, clickType) -> {
                if(queue.isAvailable()) {
                    gqm.addToQueue(p, queue);
                    p.closeInventory();
                }
            });

            buttons.add(button);
        }
    }
}
