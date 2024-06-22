package camp.pvp.practice.guis.queue;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.minigames.Minigame;
import camp.pvp.practice.games.tournaments.Tournament;
import camp.pvp.practice.guis.games.events.HostEventGui;
import camp.pvp.practice.profiles.DuelRequest;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.PreviousQueue;
import camp.pvp.practice.profiles.Rematch;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.queue.GameQueueManager;
import camp.pvp.practice.queue.QueueMenuType;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import camp.pvp.utils.guis.Gui;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class MinigameQueueGui extends ArrangedGui {

    public MinigameQueueGui(GameProfile profile) {
        super("&6Minigame Queue");

        this.setAutoUpdate(true);
        this.setDefaultBorder();

        Practice plugin = Practice.getInstance();
        GameQueueManager gqm = plugin.getGameQueueManager();

        GuiButton unrankedQueue = new GuiButton(Material.IRON_SWORD, GameQueue.Type.UNRANKED.getColor() + "&lUnranked Duel Queue");

        unrankedQueue.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        GameQueue.Type.UNRANKED.getColor() + "Playing: &f" + Practice.getInstance().getGameManager().getTotalInGame(GameQueue.GameType.DUEL, GameQueue.Type.UNRANKED),
                        GameQueue.Type.UNRANKED.getColor() + "In Queue: &f" + gqm.getTotalInQueue(GameQueue.GameType.DUEL, GameQueue.Type.UNRANKED),
                        " ",
                        "&7Click to view" + GameQueue.Type.UNRANKED.getColor() + " Unranked Duel Queues&7."
                );
            }
        });

        unrankedQueue.setAction((p, b, g, click) -> {
            new DuelQueueGui(GameQueue.Type.UNRANKED, profile).open(p);
            profile.setLastSelectedQueueMenu(QueueMenuType.DUEL_UNRANKED);
        });

        unrankedQueue.setOverrideGuiArrangement(true);
        unrankedQueue.setSlot(1);
        addButton(unrankedQueue);

        GuiButton rankedQueue = new GuiButton(Material.DIAMOND_SWORD, GameQueue.Type.RANKED.getColor() + "&lRanked Duel Queue");

        rankedQueue.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        GameQueue.Type.RANKED.getColor() + "Playing: &f" + Practice.getInstance().getGameManager().getTotalInGame(GameQueue.GameType.DUEL, GameQueue.Type.RANKED),
                        GameQueue.Type.RANKED.getColor() + "In Queue: &f" + gqm.getTotalInQueue(GameQueue.GameType.DUEL, GameQueue.Type.RANKED),
                        " ",
                        "&7Click to view " + GameQueue.Type.RANKED.getColor() + "Ranked Duel Queues&7."
                );
            }
        });

        rankedQueue.setAction((p, b, g, click) -> {
            new DuelQueueGui(GameQueue.Type.RANKED, profile).open(p);
            profile.setLastSelectedQueueMenu(QueueMenuType.DUEL_RANKED);
        });

        rankedQueue.setOverrideGuiArrangement(true);
        rankedQueue.setSlot(2);
        addButton(rankedQueue);

        GuiButton minigames = new GuiButton(Material.BOW, "&6&lMinigame Queue");
        minigames.setAction((player, guiButton, gui, clickType) -> {
            new MinigameQueueGui(profile).open(player);
            profile.setLastSelectedQueueMenu(QueueMenuType.MINIGAME);
        });
        minigames.setButtonUpdater((guiButton, gui) -> {
            guiButton.setLore(
                    "&6In Queue: &f" + Practice.getInstance().getGameQueueManager().getTotalInQueue(GameQueue.GameType.MINIGAME, GameQueue.Type.UNRANKED),
                    "&6Playing: &f" + Practice.getInstance().getGameManager().getTotalInGame(GameQueue.GameType.MINIGAME, GameQueue.Type.UNRANKED));
        });

        minigames.addGlowing();

        minigames.setOverrideGuiArrangement(true);
        minigames.setSlot(3);
        addButton(minigames);

        GuiButton events = new GuiButton(Material.NETHER_STAR, "events");
        events.setAction((player, guiButton, gui, clickType) -> {
            new HostEventGui(player, profile).open(player);
        });

        events.setButtonUpdater((guiButton, gui) -> {
            if(plugin.getGameManager().isEventRunning()) {
                Tournament tournament = plugin.getGameManager().getTournament();
                if(tournament != null) {
                    guiButton.updateName("&6&lJoin Tournament");
                    guiButton.setType(Material.DIAMOND_HELMET);
                    guiButton.setLore(
                            "&7Click to join the active tournament.");
                    return;
                }

                if(plugin.getGameManager().getActiveEvent() != null) {
                    guiButton.updateName("&6&lJoin Sumo Event");
                    guiButton.setType(Material.SLIME_BALL);
                    guiButton.setLore(
                            "&7Click to join the active Sumo event.");
                    return;
                }
            }

            guiButton.updateName("&6&lHost an Event");
            guiButton.setType(Material.NETHER_STAR);
            guiButton.setLore("&7Click to host an event.");
        });

        events.setOverrideGuiArrangement(true);
        events.setSlot(4);
        addButton(events);

        GuiButton rematch = new GuiButton(Material.BLAZE_POWDER, "&6&lRematch");
        rematch.setAction((p, guiButton, gui, clickType) -> {
            if (profile.getRematch() == null) {
                return;
            }

            DuelRequest duelRequest = profile.getDuelRequests().get(profile.getRematch().getUuid());
            if(duelRequest != null && !duelRequest.isExpired()) {
                duelRequest.startGame();
            } else {
                profile.getRematch().send();
                profile.setRematch(null);
            }

            p.closeInventory();
        });

        rematch.setButtonUpdater((guiButton, gui) -> {
            Rematch rm = profile.getRematch();
            if (rm == null) {
                guiButton.setLore(
                        "&cNo one to rematch.");
                guiButton.setType(Material.STAINED_GLASS_PANE);
                guiButton.setDurability((short) 14);
                return;
            }

            DuelRequest duelRequest = profile.getDuelRequests().get(rm.getUuid());
            if(duelRequest != null && !duelRequest.isExpired()) {
                guiButton.setType(Material.BLAZE_ROD);
                guiButton.setLore(
                        "&6Opponent: &f" + duelRequest.getSender().getName(),
                        "&6Kit: &f" + duelRequest.getKit().getDisplayName(),
                        " ",
                        "&7Click to accept rematch.");
            } else {
                guiButton.setType(Material.BLAZE_POWDER);
                guiButton.setLore(
                        "&6Opponent: &f" + rm.getName(),
                        "&6Kit: &f" + rm.getKit().getDisplayName(),
                        " ",
                        "&7Click to send rematch request.");
            }
        });

        rematch.setSlot(6);
        addButton(rematch);

        GuiButton requeue = new GuiButton(Material.PAPER, "&6&lRequeue");
        requeue.setAction((p, guiButton, gui, clickType) -> {
            PreviousQueue previousQueue = profile.getPreviousQueue();
            if (previousQueue == null) {
                return;
            }

            plugin.getGameQueueManager().addToQueue(p, previousQueue.kit(), previousQueue.queueType());
            p.closeInventory();
        });

        requeue.setButtonUpdater((guiButton, gui) -> {
            PreviousQueue previousQueue = profile.getPreviousQueue();
            if (previousQueue == null) {
                guiButton.setLore(
                        "&cNo previous queue.");
                guiButton.setType(Material.STAINED_GLASS_PANE);
                guiButton.setDurability((short) 14);
                return;
            }

            guiButton.setType(Material.PAPER);
            guiButton.setDurability((short) 0);
            guiButton.setLore(
                    "&6Queue: &f" + previousQueue.queueType().toString(),
                    "&6Kit: &f" + previousQueue.kit().getDisplayName(),
                    " ",
                    "&7Click to requeue.");
        });

        requeue.setSlot(7);
        addButton(requeue);

        rematch.setOverrideGuiArrangement(true);
        requeue.setOverrideGuiArrangement(true);

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
