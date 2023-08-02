package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RideCommand implements CommandExecutor {

    private Practice plugin;
    public RideCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("ride").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            if(args.length > 0) {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null && target != player) {
                    GameProfile targetProfile = plugin.getGameProfileManager().getLoadedProfiles().get(target.getUniqueId());
                    if(targetProfile.getState().equals(GameProfile.State.LOBBY) && profile.getState().equals(GameProfile.State.LOBBY) && !targetProfile.isStaffMode()) {
                        if(target.getPassenger() == null) {
                            target.setPassenger(player);
                            player.sendMessage(ChatColor.GREEN + "You are now riding " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + ".");
                        } else {
                            player.sendMessage(ChatColor.RED + "Someone is already riding this player!");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You cannot ride this player right now.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "The player you have specified is not on this server.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            }
        }

        return true;
    }
}
