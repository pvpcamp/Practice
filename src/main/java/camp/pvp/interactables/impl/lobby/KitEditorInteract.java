package camp.pvp.interactables.impl.lobby;

import camp.pvp.interactables.ItemInteract;
import camp.pvp.kits.DuelKit;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.buttons.GuiButton;
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
                gui.addButton(button, false);
                x++;
            }
        }

        gui.open(player);
    }
}
