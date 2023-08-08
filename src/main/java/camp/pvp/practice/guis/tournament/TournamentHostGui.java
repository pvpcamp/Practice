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
        super("Host a Tournament", 9);

        int slot = 0;
        for(DuelKit kit : DuelKit.values()) {
            if(kit.isTournament()) {
                GuiButton button = new GuiButton(kit.getIcon(), "&6" + kit.getDisplayName());
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

                button.setSlot(slot);
                this.addButton(button, false);

                slot++;
            }
        }
    }
}
