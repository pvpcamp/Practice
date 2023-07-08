package camp.pvp.interactables;

import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum InteractableItems {
    QUEUE, PIT, CREATE_PARTY, KIT_EDITOR, SETTINGS,
    STOP_SPECTATING, SHOW_PLAYERS,
    LEAVE_QUEUE;

    public InteractableItem getItem() {
        switch(this) {
            // LOBBY
            case QUEUE:
                return new InteractableItem(
                        new ItemBuilder(Material.DIAMOND_SWORD, "&6Join a Queue &7(Right Click)").create(),
                        0,
                        (player, gameProfile) -> {

                        });
            case PIT:
                return new InteractableItem(
                        new ItemBuilder(Material.GOLD_AXE, "&6Join The Pit &7(Right Click)").create(),
                        1,
                        (player, gameProfile) -> {

                        });
            case CREATE_PARTY:
                return new InteractableItem(
                        new ItemBuilder(Material.BEACON, "&6Create a Party &7(Right Click)").create(),
                        4,
                        (player, gameProfile) -> {

                        });
            case KIT_EDITOR:
                return new InteractableItem(
                        new ItemBuilder(Material.BOOK, "&6Edit Your Kits &7(Right Click)").create(),
                        7,
                        (player, gameProfile) -> {

                        });
            case SETTINGS:
                return new InteractableItem(
                        new ItemBuilder(Material.ANVIL, "&6Settings &7(Right Click)").create(),
                        8,
                        (player, gameProfile) -> {

                        });
            // LOBBY_QUEUE
            case LEAVE_QUEUE:
                return new InteractableItem(
                        new ItemBuilder(Material.REDSTONE, "&cLeave Queue &7(Right Click)").create(),
                        0,
                        (player, gameProfile) -> {

                        });
            default:
                return null;
        }
    }

    public GameProfile.State getState() {
        switch (this) {
            case LEAVE_QUEUE:
                return GameProfile.State.LOBBY_QUEUE;
            default:
                return GameProfile.State.LOBBY;

        }
    }
}
