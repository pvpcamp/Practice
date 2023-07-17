package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.PostGameInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PostGameInventoryCommand implements CommandExecutor {

    private Practice plugin;
    public PostGameInventoryCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("postgameinventory").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length > 0) {
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(args[0]);
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "The inventory ID you specified was not valid.");
                    return true;
                }

                PostGameInventory pgi = plugin.getGameManager().getPostGameInventories().get(uuid);
                if(pgi != null) {
                    pgi.getGui().open(player);
                } else {
                    player.sendMessage(ChatColor.RED + "The inventory ID you specified was not valid.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <inventory id>");
            }
        }

        return true;
    }
}
