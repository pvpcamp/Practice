package camp.pvp.practice.guis.games.events;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.sumo.SumoEvent;
import camp.pvp.practice.guis.queue.DuelQueueGui;
import camp.pvp.practice.guis.queue.MinigameQueueGui;
import camp.pvp.practice.guis.tournament.TournamentHostGui;
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
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class HostEventGui extends ArrangedGui {

    public HostEventGui(Player player, GameProfile profile) {
        super("&6Host an Event");

        this.setDefaultBorder();

        Practice plugin = Practice.instance;
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
        minigames.setAction((p, guiButton, gui, clickType) -> {
            new MinigameQueueGui(profile).open(p);
            profile.setLastSelectedQueueMenu(QueueMenuType.MINIGAME);
        });
        minigames.setButtonUpdater((guiButton, gui) -> {
            guiButton.setLore(
                    "&6In Queue: &f" + Practice.getInstance().getGameQueueManager().getTotalInQueue(GameQueue.GameType.MINIGAME, GameQueue.Type.UNRANKED),
                    "&6Playing: &f" + Practice.getInstance().getGameManager().getTotalInGame(GameQueue.GameType.MINIGAME, GameQueue.Type.UNRANKED),
                    " ",
                    "&7Click to view minigame queues.");

        });

        minigames.setOverrideGuiArrangement(true);
        minigames.setSlot(3);
        addButton(minigames);

        GuiButton events = new GuiButton(Material.NETHER_STAR, "&6&lEvents");
        events.setLore("&7You are here.");

        events.addGlowing();
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

        GuiButton sumoEvent = new GuiButton(Material.LEASH, "&6&lSumo Event");

        List<String> sumoLines = new ArrayList<>();
        sumoLines.add("&7Be the last man standing");
        sumoLines.add("&7on the platform.");
        sumoLines.add(" ");

        if (player.hasPermission("practice.events.host.sumo")) {
            sumoEvent.setCloseOnClick(true);
            sumoEvent.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    if(!plugin.getGameManager().isEventRunning()) {
                        SumoEvent event = new SumoEvent(plugin);
                        event.start();
                    } else {
                        player.sendMessage(ChatColor.RED + "There is an event already running.");
                    }
                }
            });

            sumoLines.add("&eClick to host this event.");
        } else {
            sumoLines.add("&cYou must have &e&lExplorer Rank");
            sumoLines.add("&cor higher to host this event.");
        }

        sumoEvent.setLore(sumoLines);
        addButton(sumoEvent);

        GuiButton tournament = new GuiButton(Material.DIAMOND_SWORD, "&6&lTournament");

        List<String> tournamentLines = new ArrayList<>();
        tournamentLines.add("&7Be the last man standing");
        tournamentLines.add("&7through a series of duels.");
        tournamentLines.add(" ");

        if (player.hasPermission("practice.events.host.tournament")) {
            tournament.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    if(!plugin.getGameManager().isEventRunning()) {
                        new TournamentHostGui(Practice.instance).open(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "This event is already running.");
                    }
                }
            });

            tournamentLines.add("&eClick to host this event.");
        } else {
            tournamentLines.add("&cYou must have &b&lAdventurer Rank");
            tournamentLines.add("&cor higher to host this event.");
        }

        tournament.setLore(tournamentLines);
        addButton(tournament);
    }
}
