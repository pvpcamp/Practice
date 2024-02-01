package camp.pvp.practice.guis.games.events;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.impl.events.SumoEvent;
import camp.pvp.practice.guis.tournament.TournamentHostGui;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HostEventGui extends StandardGui {

    public HostEventGui(Player player) {
        super("&6Host an Event", 27);

        this.setDefaultBackground();

        Practice plugin = Practice.instance;

        GuiButton sumoEvent = new GuiButton(Material.LEASH, "&6&lSumo Event");

        List<String> sumoLines = new ArrayList<>();
        sumoLines.add("&7Be the last man standing");
        sumoLines.add("&7on the platform.");
        sumoLines.add(" ");

        if (player.hasPermission("practice.events.host.sumo")) {
            sumoEvent.setCloseOnClick(true);
            sumoEvent.setAction(new GuiAction() {
                @Override
                public void run(Player player, Gui gui) {
                    if(!plugin.getGameManager().isEventRunning()) {
                        SumoEvent event = new SumoEvent(plugin, UUID.randomUUID());
                        event.start();
                    } else {
                        player.sendMessage(ChatColor.RED + "This event is already running.");
                    }
                }
            });

            sumoLines.add("&eClick to host this event.");
        } else {
            sumoLines.add("&cYou must have &5&lPlus Rank");
            sumoLines.add("&cor higher to host this event.");
        }

        sumoEvent.setLore(sumoLines);
        sumoEvent.setSlot(11);
        this.addButton(sumoEvent, false);

        GuiButton tournament = new GuiButton(Material.DIAMOND_SWORD, "&6&lTournament");

        List<String> tournamentLines = new ArrayList<>();
        tournamentLines.add("&7Be the last man standing");
        tournamentLines.add("&7through a series of duels.");
        tournamentLines.add(" ");

        if (player.hasPermission("practice.events.host.tournament")) {
            tournament.setAction(new GuiAction() {
                @Override
                public void run(Player player, Gui gui) {
                    if(!plugin.getGameManager().isEventRunning()) {
                        new TournamentHostGui(Practice.instance).open(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "This event is already running.");
                    }
                }
            });

            tournamentLines.add("&eClick to host this event.");
        } else {
            tournamentLines.add("&cYou must have &6&lPremium Rank");
            tournamentLines.add("&cto host this event.");
        }

        tournament.setLore(tournamentLines);
        tournament.setSlot(15);
        this.addButton(tournament, false);
    }
}
