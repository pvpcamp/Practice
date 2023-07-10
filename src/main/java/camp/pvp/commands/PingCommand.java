package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {

    private Practice plugin;
    public PingCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("ping").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.GREEN + "Your ping is " + PlayerUtils.getPing(player) + " ms.");
        }

        return true;
    }
}
