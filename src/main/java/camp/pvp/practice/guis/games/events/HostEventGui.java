package camp.pvp.practice.guis.games.events;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.sumo.SumoEvent;
import camp.pvp.practice.guis.queue.PlayGui;
import camp.pvp.practice.guis.tournament.TournamentHostGui;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class HostEventGui extends StandardGui {

    public HostEventGui(Player player) {
        super("&6Host an Event", 27);

        this.setDefaultBackground();

        Practice plugin = Practice.instance;

        GuiButton back = new GuiButton(Material.ARROW, "&c&lBack");
        back.setAction((p, b, g, click) -> new PlayGui(plugin.getGameProfileManager().getLoadedProfile(player.getUniqueId())).open(p));
        back.setLore("&7Click to return to", "&7the play menu.");
        back.setSlot(0);
        back.setOverrideGuiArrangement(true);
        addButton(back);

        GuiButton sumoEvent = new GuiButton(Material.LEASH, "&6&lSumo Event");

        List<String> sumoLines = new ArrayList<>();
        sumoLines.add("&7Be the last man standing");
        sumoLines.add("&7on the platform.");
        sumoLines.add(" ");

        if (player.hasPermission("practice.events.host.sumo")) {
            sumoEvent.setCloseOnClick(true);
            sumoEvent.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    if(!plugin.getGameManager().isEventRunning()) {
                        SumoEvent event = new SumoEvent(plugin);
                        event.start();
                    } else {
                        player.sendMessage(ChatColor.RED + "There is an event already running.");
                    }
                }
            });

            sumoLines.add("&eClick to host this event.");
        } else {
            sumoLines.add("&cYou must have &e&lExplorer Rank");
            sumoLines.add("&cor higher to host this event.");
        }

        sumoEvent.setLore(sumoLines);
        sumoEvent.setSlot(11);
        addButton(sumoEvent);

        GuiButton tournament = new GuiButton(Material.DIAMOND_SWORD, "&6&lTournament");

        List<String> tournamentLines = new ArrayList<>();
        tournamentLines.add("&7Be the last man standing");
        tournamentLines.add("&7through a series of duels.");
        tournamentLines.add(" ");

        if (player.hasPermission("practice.events.host.tournament")) {
            tournament.setAction(new GuiAction() {
                @Override
                public void run(Player player, GuiButton button, Gui gui, ClickType click) {
                    if(!plugin.getGameManager().isEventRunning()) {
                        new TournamentHostGui(Practice.instance).open(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "This event is already running.");
                    }
                }
            });

            tournamentLines.add("&eClick to host this event.");
        } else {
            tournamentLines.add("&cYou must have &b&lAdventurer Rank");
            tournamentLines.add("&cor higher to host this event.");
        }

        tournament.setLore(tournamentLines);
        tournament.setSlot(15);
        addButton(tournament);
    }
}
