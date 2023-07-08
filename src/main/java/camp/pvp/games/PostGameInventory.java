package camp.pvp.games;

import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.inventory.ItemStack;

public class PostGameInventory extends GameInventory {

    private final GameParticipant gameParticipant;
    private final ItemStack[] items, armor;
    private final StandardGui gui;

    public PostGameInventory(GameParticipant gameParticipant, ItemStack[] items, ItemStack[] armor) {
        this.gameParticipant = gameParticipant;
        this.items = items;
        this.armor = armor;

        this.gui = new StandardGui(gameParticipant.getName() + "'s Inventory", 45);
    }
}
