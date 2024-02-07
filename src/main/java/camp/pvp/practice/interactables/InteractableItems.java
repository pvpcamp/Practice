package camp.pvp.practice.interactables;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameManager;
import camp.pvp.practice.games.GameSpectator;
import camp.pvp.practice.games.sumo.SumoEventDuel;
import camp.pvp.practice.games.tournaments.Tournament;
import camp.pvp.practice.interactables.impl.evemt.EventLeaveInteract;
import camp.pvp.practice.interactables.impl.game.*;
import camp.pvp.practice.interactables.impl.lobby.EventInteract;
import camp.pvp.practice.interactables.impl.lobby.RematchInteract;
import camp.pvp.practice.interactables.impl.queue.RequeueInteract;
import camp.pvp.practice.interactables.impl.tournaments.TournamentJoinInteract;
import camp.pvp.practice.interactables.impl.tournaments.TournamentLeaveInteract;
import camp.pvp.practice.interactables.impl.tournaments.TournamentStatusInteract;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.PreviousQueue;
import camp.pvp.practice.profiles.Rematch;
import camp.pvp.practice.utils.ItemBuilder;
import camp.pvp.practice.interactables.impl.lobby.KitEditorInteract;
import camp.pvp.practice.interactables.impl.party.PartyCreateInteract;
import camp.pvp.practice.interactables.impl.lobby.SettingsInteract;
import camp.pvp.practice.interactables.impl.queue.LeaveQueueInteract;
import camp.pvp.practice.interactables.impl.queue.QueueInteract;
import camp.pvp.practice.interactables.impl.party.*;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public enum InteractableItems {
    QUEUE, EVENT, REQUEUE, REMATCH, PARTY_CREATE, KIT_EDITOR, SETTINGS,
    LEAVE_QUEUE,
    PARTY_EVENT, PARTY_SPECTATE, PARTY_KIT, PARTY_LEAVE, PARTY_SETTINGS,
    TOURNAMENT_STATUS, TOURNAMENT_LEAVE,
    EVENT_LEAVE,
    SHOW_SPECTATORS, STOP_SPECTATING, TELEPORTER, SPECTATOR_VISIBLE_TO_PLAYERS, SPECTATE_RANDOM;

    public InteractableItem getItem() {
        switch (this) {
            // LOBBY
            case QUEUE:
                return new InteractableItem(
                        new ItemBuilder(Material.DIAMOND_SWORD, "&6Join a Queue").create(), 0, new QueueInteract());
            case EVENT:
                GameManager gm = Practice.getInstance().getGameManager();
                if (gm.isEventRunning()) {
                    if (gm.getTournament() != null) {
                        if (gm.getTournament().getState().equals(Tournament.State.STARTING)) {
                            return new InteractableItem(
                                    new ItemBuilder(Material.DIAMOND, "&6Join Current Tournament").create(), 1, new TournamentJoinInteract());
                        } else {
                            return new InteractableItem(
                                    new ItemBuilder(Material.DIAMOND, "&6View Tournament Status").create(), 1, new TournamentStatusInteract());
                        }
                    }

                    if (gm.getActiveEvent() != null) {
                        return new InteractableItem(
                                new ItemBuilder(Material.EMERALD, "&6Join Current Event").create(), 1, new EventInteract());
                    }
                } else {
                    return new InteractableItem(
                            new ItemBuilder(Material.IRON_AXE, "&6Host an Event").create(), 1, new EventInteract());
                }
                return null;
            case REQUEUE:
                return new InteractableItem(
                        new ItemBuilder(Material.PAPER, "&6Play Again").create(), 3,
                        new RequeueInteract(),
                        new ItemUpdater() {
                            @Override
                            public void onUpdate(InteractableItem item, GameProfile profile) {
                                PreviousQueue queue = profile.getPreviousQueue();
                                if (queue != null) {
                                    item.updateName("&6Queue " + queue.getQueueType().toString() + " " + queue.getKit().getDisplayName());
                                }
                            }
                        }
                );
            case REMATCH:
                return new InteractableItem(
                        new ItemBuilder(Material.BLAZE_POWDER, "&6Rematch").create(), 2,
                        new RematchInteract(),
                        new ItemUpdater() {
                            @Override
                            public void onUpdate(InteractableItem item, GameProfile profile) {
                                Rematch rematch = profile.getRematch();
                                if (rematch != null) {
                                    item.updateName("&6Rematch &f" + rematch.getName());
                                }
                            }
                        }
                );
            case PARTY_CREATE:
                return new InteractableItem(
                        new ItemBuilder(Material.NETHER_STAR, "&6Create a Party").create(), 4, new PartyCreateInteract());
            case KIT_EDITOR:
                return new InteractableItem(
                        new ItemBuilder(Material.BOOK, "&6Edit Your Kits").create(), 7, new KitEditorInteract());
            case SETTINGS:
                return new InteractableItem(
                        new ItemBuilder(Material.ANVIL, "&6Settings").create(), 8, new SettingsInteract());
            // LOBBY_QUEUE
            case LEAVE_QUEUE:
                return new InteractableItem(
                        new ItemBuilder(Material.REDSTONE, "&cLeave Queue").create(), 0, new LeaveQueueInteract());
            // LOBBY_PARTY
            case PARTY_EVENT:
                return new InteractableItem(
                        new ItemBuilder(Material.GOLD_SWORD, "&6Start a Party Event").create(), 1, new PartyEventInteract());
            case PARTY_SPECTATE:
                return new InteractableItem(
                        new ItemBuilder(Material.COMPASS, "&6Spectate Party Game").create(), 1, new PartySpectateInteract());
            case PARTY_KIT:
                return new InteractableItem(
                        new ItemBuilder(Material.CHEST, "&6Customize HCF Kits").create(), 0, new PartyKitInteract());
            case PARTY_LEAVE:
                return new InteractableItem(
                        new ItemBuilder(Material.REDSTONE, "&6Leave Party").create(), 4, new PartyLeaveInteract());
            case PARTY_SETTINGS:
                return new InteractableItem(
                        new ItemBuilder(Material.PAPER, "&6Party Settings").create(), 7, new PartySettingsInteract());
            // LOBBY_TOURNAMENT
            case TOURNAMENT_STATUS:
                return new InteractableItem(
                        new ItemBuilder(Material.DIAMOND, "&6Tournament Status").create(), 0, new TournamentStatusInteract());
            case TOURNAMENT_LEAVE:
                return new InteractableItem(
                        new ItemBuilder(Material.NETHER_STAR, "&6Leave Tournament").create(), 4, new TournamentLeaveInteract());
            // LOBBY_EVENT
            case EVENT_LEAVE:
                return new InteractableItem(
                        new ItemBuilder(Material.REDSTONE, "&cLeave Event").create(), 4, new EventLeaveInteract());
            // SPECTATING
            case SHOW_SPECTATORS:
                return new InteractableItem(
                        new ItemBuilder(Material.INK_SACK, "&aShow/Hide Other Spectators").create(), 3, new ShowSpectatorsInteract(),
                        (item, profile) -> {
                            item.getItem().setDurability((short) (profile.isSpectatorVisibility() ? 10 : 8));
                            item.updateName(profile.isSpectatorVisibility() ? "&aSpectator Visibility &e(Enabled)" : "&aSpectator Visibility &c(Disabled)");
                        });
            case STOP_SPECTATING:
                return new InteractableItem(
                        new ItemBuilder(Material.BED, "&cStop Spectating").create(), 4, new StopSpectatingInteract());
            case TELEPORTER:
                return new InteractableItem(
                        new ItemBuilder(Material.WATCH, "&eTeleportation Device").create(), 5, new TeleporterInteract());
            // Staff Spectator Utilities
            case SPECTATOR_VISIBLE_TO_PLAYERS:
                return new InteractableItem(
                        new ItemBuilder(Material.STICK, "&aShow/Hide Self To Players").create(), 7, new VisibleToPlayersInteract(),
                        (item, profile) -> {
                            Game game = profile.getGame();
                            GameSpectator spectator = game.getSpectators().get(profile.getUuid());
                            item.getItem().setType(spectator.isVisibleToPlayers() ? Material.BLAZE_ROD : Material.STICK);
                            item.updateName(!spectator.isVisibleToPlayers() ? "&7Invisible to Players" : "&e&lVisible to Players");
                        });
            case SPECTATE_RANDOM:
                return new InteractableItem(
                        new ItemBuilder(Material.BLAZE_POWDER, "&6Spectate Random Game").create(), 8, new SpectateRandomInteract());
            default:
                return null;
        }
    }

    public static List<InteractableItems> getInteractableItems(GameProfile profile) {
        List<InteractableItems> items = new ArrayList<>();
        switch(profile.getState()) {
            case LOBBY:
                items.add(QUEUE);
                items.add(EVENT);

                if(profile.getRematch() != null) {
                    items.add(REMATCH);
                }

                if(profile.getPreviousQueue() != null) {
                    items.add(REQUEUE);
                }

                items.add(PARTY_CREATE);
                items.add(KIT_EDITOR);
                items.add(SETTINGS);
                break;
            case LOBBY_PARTY:
                Party party = profile.getParty();
                if(party.getMembers().get(profile.getUuid()).isLeader()) {
                    items.add(PARTY_EVENT);
                    items.add(PARTY_SETTINGS);
                }

                if(party.getGame() != null) {
                    items.add(PARTY_SPECTATE);
                }

                items.add(PARTY_KIT);
                items.add(PARTY_LEAVE);
                items.add(SETTINGS);
                break;
            case LOBBY_QUEUE:
                items.add(LEAVE_QUEUE);
                break;
            case LOBBY_TOURNAMENT:
                items.add(TOURNAMENT_LEAVE);
                items.add(TOURNAMENT_STATUS);
                items.add(SETTINGS);
                break;
            case LOBBY_EVENT:
                items.add(EVENT_LEAVE);
                break;
            case SPECTATING:
                items.add(STOP_SPECTATING);

                if(!(profile.getGame() instanceof SumoEventDuel)) {
                    items.add(SHOW_SPECTATORS);
                    items.add(TELEPORTER);
                }

                if(profile.getPlayer().hasPermission("practice.staff")) {
                    items.add(SPECTATOR_VISIBLE_TO_PLAYERS);
                    items.add(SPECTATE_RANDOM);
                }
                break;
        }

        return items;
    }
}
