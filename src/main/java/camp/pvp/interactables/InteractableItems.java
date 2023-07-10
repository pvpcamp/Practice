package camp.pvp.interactables;

import camp.pvp.interactables.impl.game.StopSpectatingInteract;
import camp.pvp.interactables.impl.lobby.KitEditorInteract;
import camp.pvp.interactables.impl.lobby.PartyCreateInteract;
import camp.pvp.interactables.impl.lobby.PitInteract;
import camp.pvp.interactables.impl.lobby.SettingsInteract;
import camp.pvp.interactables.impl.queue.LeaveQueueInteract;
import camp.pvp.interactables.impl.queue.QueueInteract;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.ItemBuilder;
import org.bukkit.Material;

public enum InteractableItems {
    QUEUE, PIT, PARTY_CREATE, KIT_EDITOR, SETTINGS,
    STOP_SPECTATING,
    LEAVE_QUEUE;

    public InteractableItem getItem() {
        switch(this) {
            // LOBBY
            case QUEUE:
                return new InteractableItem(
                        new ItemBuilder(Material.DIAMOND_SWORD, "&6Join a Queue &7(Right Click)").create(), 0, new QueueInteract());
            case PIT:
                return new InteractableItem(
                        new ItemBuilder(Material.GOLD_AXE, "&6Join The Pit &7(Right Click)").create(), 1, new PitInteract());
            case PARTY_CREATE:
                return new InteractableItem(
                        new ItemBuilder(Material.BEACON, "&6Create a Party &7(Right Click)").create(), 4, new PartyCreateInteract());
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
            case PIT:
            case PARTY_CREATE:
            case KIT_EDITOR:
            case SETTINGS:
                return GameProfile.State.LOBBY;
            case LEAVE_QUEUE:
                return GameProfile.State.LOBBY_QUEUE;
            case STOP_SPECTATING:
                return GameProfile.State.SPECTATING;
            default:
                return GameProfile.State.IN_GAME;

        }
    }
}
