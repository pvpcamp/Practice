package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.utils.Colors;
import camp.pvp.utils.PlayerUtils;
import org.bukkit.Bukkit;
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
            int ping = PlayerUtils.getPing(player);

            if(args.length > 0) {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    int targetPing = PlayerUtils.getPing(target);
                    int difference = targetPing - ping;
                    StringBuilder sb = new StringBuilder();
                    sb.append("&eYour Ping: &f" + ping + " ms");
                    sb.append("\n&a" + target.getName() + "'s Ping: &f" + targetPing + " ms");
                    sb.append("\n&6Difference: &f" + (difference > 0 ? "+" + difference : difference) + " ms");

                    player.sendMessage(Colors.get(sb.toString()));
                } else {
                    player.sendMessage(ChatColor.RED + "The target you specified was not found.");
                }
            } else {
                player.sendMessage(Colors.get("&aYour Ping: &f" + ping + " ms"));
            }
        }

        return true;
    }
}
