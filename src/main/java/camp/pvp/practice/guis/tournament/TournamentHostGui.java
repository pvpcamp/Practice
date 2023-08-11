package camp.pvp.practice.guis.tournament;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.entity.Player;

public class TournamentHostGui extends StandardGui{
    public TournamentHostGui(Practice plugin) {
        super("Host a Tournament", 36);

        for(DuelKit kit : DuelKit.values()) {
            GuiButton button = new GuiButton(kit.getIcon(), "&6" + kit.getDisplayName());
            if(kit.isTournament()) {
                button.setLore(
                        "&7Next, select a team size for ",
                        "&7the &f" + kit.getDisplayName() + " &7tournament."
                );

                button.setAction(new GuiAction() {
                    @Override
                    public void run(Player player, Gui gui) {
                        TournamentTeamSizeGui teamSizeGUI = new TournamentTeamSizeGui(plugin, kit);
                        teamSizeGUI.open(player);
                    }
                });
            } else {
                button.updateName("&7&o" + kit.getDisplayName());
                button.setLore("&cYou cannot host a tournament with this kit.");
            }

            button.setSlot(kit.getGuiSlot());
            this.addButton(button, false);
        }
    }
}
