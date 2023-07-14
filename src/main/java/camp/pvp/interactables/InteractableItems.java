package camp.pvp.interactables;

import camp.pvp.interactables.impl.game.StopSpectatingInteract;
import camp.pvp.interactables.impl.lobby.KitEditorInteract;
import camp.pvp.interactables.impl.lobby.PartyCreateInteract;
import camp.pvp.interactables.impl.lobby.PitInteract;
import camp.pvp.interactables.impl.lobby.SettingsInteract;
import camp.pvp.interactables.impl.party.*;
import camp.pvp.interactables.impl.queue.LeaveQueueInteract;
import camp.pvp.interactables.impl.queue.QueueInteract;
import camp.pvp.parties.Party;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public enum InteractableItems {
    QUEUE, PARTY_CREATE, KIT_EDITOR, SETTINGS,
    LEAVE_QUEUE,
    PARTY_EVENT, PARTY_SPECTATE, PARTY_KIT, PARTY_LEAVE, PARTY_SETTINGS,
    STOP_SPECTATING;

    public InteractableItem getItem() {
        switch(this) {
            // LOBBY
            case QUEUE:
                return new InteractableItem(
                        new ItemBuilder(Material.DIAMOND_SWORD, "&6Join a Queue &7(Right Click)").create(), 0, new QueueInteract());
            case PARTY_CREATE:
                return new InteractableItem(
                        new ItemBuilder(Material.NETHER_STAR, "&6Create a Party &7(Right Click)").create(), 4, new PartyCreateInteract());
            case KIT_EDITOR:
                return new InteractableItem(
                        new ItemBuilder(Material.BOOK, "&6Edit Your Kits &7(Right Click)").create(), 7, new KitEditorInteract());
            case SETTINGS:
                return new InteractableItem(
                        new ItemBuilder(Material.ANVIL, "&6Settings &7(Right Click)").create(), 8, new SettingsInteract());
            // LOBBY_QUEUE
            case LEAVE_QUEUE:
                return new InteractableItem(
                        new ItemBuilder(Material.REDSTONE, "&cLeave Queue &7(Right Click)").create(), 0, new LeaveQueueInteract());
            // LOBBY_PARTY
            case PARTY_EVENT:
                return new InteractableItem(
                        new ItemBuilder(Material.GOLD_SWORD, "&6Start a Party Event &7(Right Click)").create(), 1, new PartyEventInteract());
            case PARTY_SPECTATE:
                return new InteractableItem(
                        new ItemBuilder(Material.COMPASS, "&6Spectate Party Game &7(Right Click)").create(), 1, new PartySpectateInteract());
            case PARTY_KIT:
                return new InteractableItem(
                        new ItemBuilder(Material.CHEST, "&6Customize HCF Kits &7(Right Click)").create(), 0, new PartyKitInteract());
            case PARTY_LEAVE:
                return new InteractableItem(
                        new ItemBuilder(Material.REDSTONE, "&6Leave Party &7(Right Click)").create(), 4, new PartyLeaveInteract());
            case PARTY_SETTINGS:
                return new InteractableItem(
                        new ItemBuilder(Material.PAPER, "&6Party Settings &7(Right Click)").create(), 7, new PartySettingsInteract());
            // SPECTATING
            case STOP_SPECTATING:
                return new InteractableItem(
                        new ItemBuilder(Material.REDSTONE, "&cStop Spectating &7(Right Click)").create(), 4, new StopSpectatingInteract());
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
            case SPECTATING:
                items.add(STOP_SPECTATING);
                break;
        }

        return items;
    }
}
