package camp.pvp.interactables.impl.lobby;

import camp.pvp.interactables.ItemInteract;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SettingsInteract implements ItemInteract {

    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        StandardGui gui = new StandardGui("Settings", 9);
        GuiButton comingSoon = new GuiButton(Material.WOOL, "&6Coming soon!");
        comingSoon.setSlot(4);
        gui.addButton(comingSoon, false);

        gui.open(player);
    }
}
