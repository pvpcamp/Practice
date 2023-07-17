package camp.pvp.practice.interactables.impl.lobby;

import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.entity.Player;

public class KitEditorInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        StandardGui gui = new StandardGui("Edit a Kit", 9);

        int x = 0;
        for(DuelKit kit : DuelKit.values()) {
            if(kit.isEditable()) {
                GuiButton button = new GuiButton(kit.getIcon(), kit.getColor() + kit.getDisplayName());
                button.setLore("&7Click to edit " + kit.getColor() + kit.getDisplayName() + "&7.");
                button.setSlot(x);

                button.setAction(new GuiAction() {
                    @Override
                    public void run(Player player, Gui gui) {
                        gameProfile.setEditingKit(kit);
                        gameProfile.playerUpdate();
                        kit.apply(player);
                        player.getInventory().setArmorContents(null);
                    }
                });

                gui.addButton(button, false);
                x++;
            }
        }

        gui.open(player);
    }
}
