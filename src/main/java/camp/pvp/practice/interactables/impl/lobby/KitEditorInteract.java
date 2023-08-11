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
        StandardGui gui = new StandardGui("Edit a Kit", 36);

        for(DuelKit kit : DuelKit.values()) {
            GuiButton button = new GuiButton(kit.getIcon(), "&6" + kit.getDisplayName());
            button.setSlot(kit.getGuiSlot());
            if(kit.isEditable()) {
                button.setLore("&7Click to edit &f" + kit.getDisplayName() + "&7.");

                button.setAction(new GuiAction() {
                    @Override
                    public void run(Player player, Gui gui) {
                        gameProfile.setEditingKit(kit);
                        gameProfile.playerUpdate(true);
                        kit.apply(player);
                        player.getInventory().setArmorContents(null);
                    }
                });

                gui.addButton(button, false);
            } else {
                button.updateName("&7&o" + kit.getDisplayName());
                button.setLore("&cYou cannot edit this kit.");
            }

            gui.addButton(button, false);
        }

        gui.open(player);
    }
}
