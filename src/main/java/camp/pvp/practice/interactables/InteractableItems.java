package camp.pvp.practice.interactables;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameManager;
import camp.pvp.practice.games.GameSpectator;
import camp.pvp.practice.games.sumo.SumoEventDuel;
import camp.pvp.practice.games.tournaments.Tournament;
import camp.pvp.practice.interactables.impl.event.EventLeaveInteract;
import camp.pvp.practice.interactables.impl.game.*;
import camp.pvp.practice.interactables.impl.lobby.*;
import camp.pvp.practice.interactables.impl.tournaments.TournamentJoinInteract;
import camp.pvp.practice.interactables.impl.tournaments.TournamentLeaveInteract;
import camp.pvp.practice.interactables.impl.tournaments.TournamentStatusInteract;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.PreviousQueue;
import camp.pvp.practice.profiles.Rematch;
import camp.pvp.practice.utils.ItemBuilder;
import camp.pvp.practice.interactables.impl.party.PartyCreateInteract;
import camp.pvp.practice.interactables.impl.queue.LeaveQueueInteract;
import camp.pvp.practice.interactables.impl.queue.QueueInteract;
import camp.pvp.practice.interactables.impl.party.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public enum InteractableItems {
    QUEUE, PARTY_CREATE, SERVERS, PROFILE, KIT_EDITOR, SETTINGS,
    LEAVE_QUEUE,
    PARTY_EVENT, PARTY_SPECTATE, PARTY_KIT, PARTY_LEAVE, PARTY_SETTINGS, PARTY_INFO,
    TOURNAMENT_STATUS, TOURNAMENT_LEAVE,
    EVENT_LEAVE,
    SHOW_SPECTATORS, STOP_SPECTATING, TELEPORTER, SPECTATOR_VISIBLE_TO_PLAYERS, SPECTATE_RANDOM;

    public InteractableItem getItem() {
        switch (this) {
            // LOBBY
            case QUEUE:
                return new InteractableItem(
                        new ItemBuilder(Material.GOLD_SWORD, "&6Play").create(), 0, new QueueInteract());
            case PARTY_CREATE:
                return new InteractableItem(
                        new ItemBuilder(Material.NAME_TAG, "&6Create a Party").create(), 1, new PartyCreateInteract());
            case SERVERS:
                return new InteractableItem(
                        new ItemBuilder(Material.WATCH, "&6&lNetwork Navigator").create(), 4, (player, gameProfile) -> {
                            player.performCommand("servers");
                        });
            case PROFILE:
                return new InteractableItem(
                        new ItemBuilder(Material.SKULL_ITEM, "&6My Profile").create(), 6, new MyProfileInteract(), (item, profile) -> {
                            ItemStack i = item.getItem();
                            i.setDurability((short) 3);

                            SkullMeta meta = (SkullMeta) i.getItemMeta();
                            meta.setOwner(profile.getName());
                            i.setItemMeta(meta);
                        });
            case KIT_EDITOR:
                return new InteractableItem(
                        new ItemBuilder(Material.BOOK, "&6Edit Your Kits").create(), 7, new KitEditorInteract());
            case SETTINGS:
                return new InteractableItem(
                        new ItemBuilder(Material.REDSTONE_COMPARATOR, "&6Customize Your Settings").create(), 8, new SettingsInteract());
            // LOBBY_QUEUE
            case LEAVE_QUEUE:
                return new InteractableItem(
                        new ItemBuilder(Material.REDSTONE, "&cLeave Queue").create(), 0, new LeaveQueueInteract());
            // LOBBY_PARTY
            case PARTY_EVENT:
                return new InteractableItem(
                        new ItemBuilder(Material.DIAMOND_SWORD, "&6Start a Party Event").create(), 0, new PartyEventInteract());
            case PARTY_SPECTATE:
                return new InteractableItem(
                        new ItemBuilder(Material.COMPASS, "&6Spectate Party Game").create(), 0, new PartySpectateInteract());
            case PARTY_SETTINGS:
                return new InteractableItem(
                        new ItemBuilder(Material.TRIPWIRE_HOOK, "&6Party Settings").create(), 1, new PartySettingsInteract());
            case PARTY_LEAVE:
                return new InteractableItem(
                        new ItemBuilder(Material.NETHER_STAR, "&6Leave Party").create(), 4, new PartyLeaveInteract());
            case PARTY_INFO:
                return new InteractableItem(
                        new ItemBuilder(Material.PAPER, "&6Party Info").create(), 7, (player, gameProfile) -> {
                            player.performCommand("party info");
                        });
            case PARTY_KIT:
                return new InteractableItem(
                        new ItemBuilder(Material.CHEST, "&6Customize HCF Kits").create(), 6, new PartyKitInteract());
            // LOBBY_TOURNAMENT
            case TOURNAMENT_STATUS:
                return new InteractableItem(
                        new ItemBuilder(Material.DIAMOND, "&6Tournament Status").create(), 0, new TournamentStatusInteract());
            case TOURNAMENT_LEAVE:
                return new InteractableItem(
                        new ItemBuilder(Material.NETHER_STAR, "&cLeave Tournament").create(), 4, new TournamentLeaveInteract());
            // LOBBY_EVENT
            case EVENT_LEAVE:
                return new InteractableItem(
                        new ItemBuilder(Material.NETHER_STAR, "&cLeave Event").create(), 4, new EventLeaveInteract());
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
                items.add(PARTY_CREATE);
                items.add(SERVERS);
                items.add(PROFILE);
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

                items.add(PARTY_INFO);
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

                Player player = profile.getPlayer();
                if(player.hasPermission("practice.spectator.visible")) {
                    items.add(SPECTATOR_VISIBLE_TO_PLAYERS);
                }

                if(profile.getPlayer().hasPermission("practice.staff")) {
                    items.add(SPECTATE_RANDOM);
                }
                break;
        }

        return items;
    }
}
