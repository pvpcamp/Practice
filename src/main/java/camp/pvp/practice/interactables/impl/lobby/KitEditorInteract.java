package camp.pvp.practice.interactables.impl.lobby;

import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class KitEditorInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        ArrangedGui gui = new ArrangedGui("&6Edit a Kit");

        for(GameKit kit : GameKit.values()) {
            if(!kit.isEditable()) continue;

            GuiButton button = new GuiButton(kit.getIcon(), "&6&l" + kit.getDisplayName());
            button.setLore("&7Click to edit &f" + kit.getDisplayName() + "&7.");

            button.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    gameProfile.setEditingKit(kit);
                    gameProfile.playerUpdate(true);
                    kit.apply(player);
                    player.getInventory().setArmorContents(null);
                }
            });

            gui.addButton(button);
        }

        gui.open(player);
    }
}
