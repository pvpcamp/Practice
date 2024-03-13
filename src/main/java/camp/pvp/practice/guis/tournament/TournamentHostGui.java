package camp.pvp.practice.guis.tournament;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class TournamentHostGui extends ArrangedGui {
    public TournamentHostGui(Practice plugin) {
        super("&6Host a Tournament");

        for(GameKit kit : GameKit.values()) {

            if(!kit.getBaseKit().isTournament()) continue;

            GuiButton button = new GuiButton(kit.getBaseKit().getIcon(), "&6" + kit.getDisplayName());

            button.setLore(
                    "&7Next, select a team size for ",
                    "&7the &f" + kit.getDisplayName() + " &7tournament."
            );

            button.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton guiButton, Gui gui, ClickType clickType) {
                    TournamentTeamSizeGui teamSizeGUI = new TournamentTeamSizeGui(plugin, kit);
                    teamSizeGUI.open(player);
                }
            });

            addButton(button, false);
        }
    }
}
