package camp.pvp.practice.commands;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
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
            sender.sendMessage(ChatColor.GREEN + "Coming soon!");
        }

        return true;
    }
}
