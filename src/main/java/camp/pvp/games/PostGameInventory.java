package camp.pvp.games;

import camp.pvp.Practice;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter @Setter
public class PostGameInventory extends GameInventory {

    private final GameParticipant gameParticipant;
    private final UUID uuid;
    private final ItemStack[] items, armor;
    private final StandardGui gui;

    public PostGameInventory(UUID uuid, GameParticipant gameParticipant, ItemStack[] items, ItemStack[] armor) {
        this.uuid = uuid;
        this.gameParticipant = gameParticipant;
        this.items = items;
        this.armor = armor;

        this.gui = new StandardGui(gameParticipant.getName() + "'s Inventory", 54);

        for(int x = 0; x < 36; x++) {
            if(items[x] != null && !items[x].getType().equals(Material.AIR)) {
                GuiButton button = new GuiButton(items[x]);
                if (x < 9) {
                    button.setSlot(x + 27);
                } else {
                    button.setSlot(x - 9);
                }

                gui.addButton(button, false);
            }
        }

        for(int x = 0; x < 4; x++) {
            if(armor[x] != null && !armor[x].getType().equals(Material.AIR)) {
                GuiButton button = new GuiButton(armor[x]);
                button.setSlot(x + 36);
                gui.addButton(button, false);
            }
        }
    }

    public void setOpponentInventory(GameParticipant opponentParticipant, PostGameInventory postGameInventory) {
        GuiButton opponentInventory = new GuiButton(Material.MAP, "&aOpen " + opponentParticipant.getName() + "'s Inventory");
        opponentInventory.setAction((player, gui) -> postGameInventory.getGui().open(player));

        opponentInventory.setSlot(49);
        gui.addButton(opponentInventory, true);
    }
}
