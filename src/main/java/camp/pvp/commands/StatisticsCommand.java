package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.profiles.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatisticsCommand implements CommandExecutor {

    private Practice plugin;
    public StatisticsCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getCommand("stats").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile target = null;
            if(args.length > 0) {
                target = plugin.getGameProfileManager().find(args[0], false);
                if(target == null) {
                    player.sendMessage(ChatColor.RED + "The player you specified was not found.");
                    return true;
                }
            } else {
                target = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            }

            player.sendMessage(target.getUuid().toString());
        }

        return true;
    }
}
