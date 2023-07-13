package camp.pvp.interactables;

import camp.pvp.interactables.impl.game.StopSpectatingInteract;
import camp.pvp.interactables.impl.lobby.KitEditorInteract;
import camp.pvp.interactables.impl.lobby.PartyCreateInteract;
import camp.pvp.interactables.impl.lobby.PitInteract;
import camp.pvp.interactables.impl.lobby.SettingsInteract;
import camp.pvp.interactables.impl.party.PartyEventInteract;
import camp.pvp.interactables.impl.party.PartyKitInteract;
import camp.pvp.interactables.impl.party.PartyLeaveInteract;
import camp.pvp.interactables.impl.party.PartySettingsInteract;
import camp.pvp.interactables.impl.queue.LeaveQueueInteract;
import camp.pvp.interactables.impl.queue.QueueInteract;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.ItemBuilder;
import org.bukkit.Material;

public enum InteractableItems {
    QUEUE, PARTY_CREATE, KIT_EDITOR, SETTINGS,
    LEAVE_QUEUE,
    PARTY_EVENT, PARTY_KIT, PARTY_LEAVE, PARTY_SETTINGS,
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
                        new ItemBuilder(Material.GOLD_SWORD, "&6Start a Party Event &7(Right Click)").create(), 0, new PartyEventInteract());
            case PARTY_KIT:
                return new InteractableItem(
                        new ItemBuilder(Material.CHEST, "&6Customize HCF Kits &7(Right Click)").create(), 1, new PartyKitInteract());
            case PARTY_LEAVE:
                return new InteractableItem(
                        new ItemBuilder(Material.REDSTONE, "&6Leave Party &7(Right Click)").create(), 4, new PartyLeaveInteract());
            case PARTY_SETTINGS:
                return new InteractableItem(
                        new ItemBuilder(Material.PAPER, "&6Party Settings &7(Right Click)").create(), 8, new PartySettingsInteract());
            // SPECTATING
            case STOP_SPECTATING:
                return new InteractableItem(
                        new ItemBuilder(Material.REDSTONE, "&cStop Spectating &7(Right Click)").create(), 4, new StopSpectatingInteract());
            default:
                return null;
        }
    }

    public GameProfile.State getState() {
        switch (this) {
            case QUEUE:
            case PARTY_CREATE:
            case KIT_EDITOR:
            case SETTINGS:
                return GameProfile.State.LOBBY;
            case LEAVE_QUEUE:
                return GameProfile.State.LOBBY_QUEUE;
            case STOP_SPECTATING:
                return GameProfile.State.SPECTATING;
            case PARTY_EVENT:
            case PARTY_KIT:
            case PARTY_LEAVE:
            case PARTY_SETTINGS:
                return GameProfile.State.LOBBY_PARTY;
            default:
                return GameProfile.State.IN_GAME;

        }
    }
}
