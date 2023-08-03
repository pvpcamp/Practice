package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.impl.events.GameEvent;
import camp.pvp.practice.guis.games.events.HostEventGui;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventCommand implements CommandExecutor {

    private Practice plugin;
    public EventCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("event").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            GameEvent event = plugin.getGameManager().getActiveEvent();
            if(args.length > 0) {
                switch(args[0].toLowerCase()) {
                    case "join":
                        if(event == null) {
                            player.sendMessage(ChatColor.RED + "There is not an event running at this time.");
                            return true;
                        }

                        if(profile.getGame() != null) {
                            player.sendMessage(ChatColor.RED + "You are already in a game.");
                            return true;
                        }

                        if(event.getState().equals(Game.State.STARTING)) {
                            event.join(player);
                        } else {
                            event.spectateStart(player);
                        }

                        return true;
                    case "leave":
                        if(event == null) {
                            player.sendMessage(ChatColor.RED + "There is not an event running at this time.");
                            return true;
                        }

                        if(profile.getGame() == null) {
                            player.sendMessage(ChatColor.RED + "You are not in a game.");
                            return true;
                        }

                        if(!profile.getGame().equals(event)) {
                            player.sendMessage(ChatColor.RED + "You are not participating in the active event.");
                            return true;
                        }

                        event.leave(player);

                        return true;
                    case "host":
                        if(plugin.getGameManager().isEventRunning()) {
                            player.sendMessage(ChatColor.RED + "There is already an event running.");
                            return true;
                        }

                        new HostEventGui(player).open(player);
                        return true;
                    case "timer":
                        if(args.length > 1) {
                            if(player.hasPermission("practice.events.manage")) {
                                if(event != null) {
                                    int i;
                                    try {
                                        i = Integer.parseInt(args[1]);
                                    } catch (NumberFormatException ignored) {
                                        player.sendMessage(ChatColor.RED + "Invalid time.");
                                        return true;
                                    }

                                    event.setTimer(i);
                                } else {
                                    player.sendMessage(ChatColor.RED + "There is not an event running at this time.");
                                }
                                return true;
                            }
                        }
                        break;
                }
            }

            StringBuilder help = new StringBuilder();
            help.append("&6&l/event &r&6Help");
            help.append("\n&6/event join &7- &fJoin the currently active event.");
            help.append("\n&6/event leave &7- &fLeave the event.");
            help.append("\n&6/event host &7- &fOpens the GUI to host an event.");

            if(player.hasPermission("practice.events.manage")) {
                help.append("\n&6/event timer <timer> &7- &fSets the event timer.");
            }

            player.sendMessage(Colors.get(help.toString()));
        }

        return true;
    }
}
