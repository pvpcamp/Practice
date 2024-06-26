package camp.pvp.practice.guis.queue;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.tournaments.Tournament;
import camp.pvp.practice.guis.games.events.HostEventGui;
import camp.pvp.practice.profiles.DuelRequest;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.PreviousQueue;
import camp.pvp.practice.profiles.Rematch;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;

public class PlayGui extends StandardGui {

    private final static Practice plugin = Practice.getInstance();

    public PlayGui(GameProfile gameProfile) {
        super("&6Welcome to &lPvP Camp", 27);

        this.setDefaultBackground();
        this.setAutoUpdate(true);

        GuiButton duelQueue = new GuiButton(Material.DIAMOND_SWORD, "&6&lDuel Queue");
        duelQueue.setAction((player, guiButton, gui, clickType) -> {
            new DuelQueueGui(gameProfile.getLastSelectedQueueType(), gameProfile).open(player);
        });
        duelQueue.setButtonUpdater((guiButton, gui) -> {
            guiButton.setLore(
                    "&7Casual and competitive",
                    "&71v1 matches.",
                    " ",
                    GameQueue.Type.UNRANKED.getColor() + "&lUnranked Queue",
                    GameQueue.Type.UNRANKED.getColor() + "Playing: &f" + plugin.getGameManager().getTotalInGame(GameQueue.GameType.DUEL, GameQueue.Type.UNRANKED),
                    GameQueue.Type.UNRANKED.getColor() + "In Queue: &f" + plugin.getGameQueueManager().getTotalInQueue(GameQueue.GameType.DUEL, GameQueue.Type.UNRANKED),
                    " ",
                    GameQueue.Type.RANKED.getColor() + "&lRanked Queue",
                    GameQueue.Type.RANKED.getColor() + "Playing: &f" + plugin.getGameManager().getTotalInGame(GameQueue.GameType.DUEL, GameQueue.Type.RANKED),
                    GameQueue.Type.RANKED.getColor() + "In Queue: &f" + plugin.getGameQueueManager().getTotalInQueue(GameQueue.GameType.DUEL, GameQueue.Type.RANKED),
                    " ",
                    "&7Click to view duel queues.");
        });

        duelQueue.setSlot(11);
        addButton(duelQueue);

        GuiButton requeue = new GuiButton(Material.PAPER, "&6&lRequeue");
        requeue.setAction((player, guiButton, gui, clickType) -> {
            PreviousQueue previousQueue = gameProfile.getPreviousQueue();
            if (previousQueue == null) {
                return;
            }

            plugin.getGameQueueManager().addToQueue(player, previousQueue.kit(), previousQueue.queueType());
            player.closeInventory();
        });

        requeue.setButtonUpdater((guiButton, gui) -> {
            PreviousQueue previousQueue = gameProfile.getPreviousQueue();
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

        requeue.setSlot(12);
        addButton(requeue);

        GuiButton rematch = new GuiButton(Material.BLAZE_POWDER, "&6&lRematch");
        rematch.setAction((player, guiButton, gui, clickType) -> {
            if (gameProfile.getRematch() == null) {
                return;
            }

            DuelRequest duelRequest = gameProfile.getDuelRequests().get(gameProfile.getRematch().getUuid());
            if(duelRequest != null && !duelRequest.isExpired()) {
                duelRequest.startGame();
            } else {
                gameProfile.getRematch().send();
                gameProfile.setRematch(null);
            }

            player.closeInventory();
        });

        rematch.setButtonUpdater((guiButton, gui) -> {
            Rematch rm = gameProfile.getRematch();
            if (rm == null) {
                guiButton.setLore(
                        "&cNo one to rematch.");
                guiButton.setType(Material.STAINED_GLASS_PANE);
                guiButton.setDurability((short) 14);
                return;
            }

            DuelRequest duelRequest = gameProfile.getDuelRequests().get(rm.getUuid());
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

        rematch.setSlot(13);
        addButton(rematch);

        GuiButton minigames = new GuiButton(Material.BOW, "&6&lMinigame Queue");
        minigames.setAction((player, guiButton, gui, clickType) -> {
            new MinigameQueueGui(gameProfile).open(player);
        });
        minigames.setButtonUpdater((guiButton, gui) -> {
            guiButton.setLore(
                    "&7Casual, but exciting minigames.",
                    " ",
                    "&6In Queue: &f" + plugin.getGameQueueManager().getTotalInQueue(GameQueue.GameType.MINIGAME, GameQueue.Type.UNRANKED),
                    "&6Playing: &f" + plugin.getGameManager().getTotalInGame(GameQueue.GameType.MINIGAME, GameQueue.Type.UNRANKED),
                    " ",
                    "&7Click to view minigame queues.");
        });

        minigames.setSlot(14);
        addButton(minigames);

        GuiButton events = new GuiButton(Material.NETHER_STAR, "events");
        events.setAction((player, guiButton, gui, clickType) -> {

            if(plugin.getGameManager().isEventRunning()) {
                if(plugin.getGameManager().getTournament() != null) {
                    plugin.getGameManager().getTournament().join(player);
                    player.closeInventory();
                    return;
                }

                if(plugin.getGameManager().getActiveEvent() != null) {
                    plugin.getGameManager().getActiveEvent().join(player);
                    player.closeInventory();
                }
            } else {
                new HostEventGui(player).open(player);
            }
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

        events.setSlot(15);
        addButton(events);

    }
}
