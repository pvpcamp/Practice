package camp.pvp.practice.guis.tournament;

import camp.pvp.practice.games.tournaments.Tournament;
import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TournamentTeamSizeGui extends StandardGui {

    public TournamentTeamSizeGui(Practice plugin, DuelKit duelKit) {
        super("Choose a Team Size", 27);

        GuiButton selectedKit = new GuiButton(duelKit.getIcon(), "&aSelected Kit: &f" + duelKit.getColor() + duelKit.getDisplayName());
        selectedKit.setSlot(13);
        this.addButton(selectedKit, false);

        GuiButton singles = new GuiButton(Material.ARROW, "&6&lSingles &7(1v1)");
        singles.setCloseOnClick(true);
        singles.setAction(new GuiAction() {
            @Override
            public void run(Player player, Gui gui) {
                if(plugin.getGameManager().getTournament() == null || plugin.getGameManager().getTournament().getState().equals(Tournament.State.ENDED)) {
                    Tournament tournament = new Tournament(plugin, duelKit, 1, 64);
                    plugin.getGameManager().setTournament(tournament);

                    player.sendMessage(ChatColor.GREEN + "You have started a new tournament.");
                    tournament.start();
                } else {
                    player.sendMessage(ChatColor.RED + "There is already a tournament running.");
                }
            }
        });

        singles.setSlot(11);
        this.addButton(singles, false);

        GuiButton doubles = new GuiButton(Material.ARROW, "&9&lDoubles &7(2v2)");
        doubles.setLore("&7&oComing soon!");
//        doubles.setCloseOnClick(true);
//        doubles.setAction(new GuiAction() {
//            @Override
//            public void run(Player player, Gui gui) {
//                if(plugin.getGameManager().getTournament() == null) {
//                    Tournament tournament = new Tournament(plugin, duelKit, 2, 128);
//                    plugin.getGameManager().setTournament(tournament);
//
//                    player.sendMessage(ChatColor.GREEN + "You have started a new tournament.");
//                    tournament.start();
//                }
//            }
//        });

        doubles.setSlot(15);
        this.addButton(doubles, false);
    }
}
