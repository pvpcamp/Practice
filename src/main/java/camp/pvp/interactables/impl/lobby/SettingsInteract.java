package camp.pvp.interactables.impl.lobby;

import camp.pvp.interactables.ItemInteract;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.buttons.AbstractButtonUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SettingsInteract implements ItemInteract {

    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        StandardGui gui = new StandardGui("Settings", 9);

        GuiButton spectatorVisibility = new GuiButton(Material.REDSTONE_TORCH_ON, "&6Spectator Visibility");
        spectatorVisibility.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                gameProfile.setSpectatorVisibility(!gameProfile.isSpectatorVisibility());
                gui.updateGui();
            }
        });
        spectatorVisibility.setButtonUpdater(new AbstractButtonUpdater() {
            @Override
            public void update(GuiButton guiButton, Gui gui) {
                guiButton.setLore(
                        "&7Would you like to see other",
                        "&7spectators while spectating?",
                        "&aCurrent Setting: &f" + (gameProfile.isSpectatorVisibility() ? "Enabled" : "Disabled"));
            }
        });
        spectatorVisibility.setSlot(0);
        gui.addButton(spectatorVisibility, false);

        gui.open(player);
    }
}
