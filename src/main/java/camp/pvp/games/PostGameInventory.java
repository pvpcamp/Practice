package camp.pvp.games;

import camp.pvp.utils.guis.StandardGui;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter @Setter
public class PostGameInventory extends GameInventory {

    private final GameParticipant gameParticipant;
    private final UUID uuid;
    private final ItemStack[] items, armor;
    private final StandardGui gui;
    private GameInventory opponentInventory;

    public PostGameInventory(UUID uuid, GameParticipant gameParticipant, ItemStack[] items, ItemStack[] armor) {
        this.uuid = uuid;
        this.gameParticipant = gameParticipant;
        this.items = items;
        this.armor = armor;

        this.gui = new StandardGui(gameParticipant.getName() + "'s Inventory", 45);
    }
}
