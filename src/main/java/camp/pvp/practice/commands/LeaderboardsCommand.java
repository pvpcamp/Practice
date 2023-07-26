package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import camp.pvp.practice.guis.statistics.LeaderboardsGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaderboardsCommand implements CommandExecutor {

    private Practice plugin;
    public LeaderboardsCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("leaderboards").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            new LeaderboardsGui().open(player);
        }

        return true;
    }
}
