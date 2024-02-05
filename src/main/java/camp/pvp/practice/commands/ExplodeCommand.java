package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExplodeCommand implements CommandExecutor {

    private Practice plugin;
    public ExplodeCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getCommand("explode").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0) {
            sender.sendMessage("Usage: /explode <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            sender.sendMessage(ChatColor.RED + "The target you specified is not on this server.");
            return true;
        }

        target.sendMessage(ChatColor.DARK_RED + "You go boom.");
        target.damage(5D);

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.canSee(target)) {
                player.playEffect(target.getLocation(), org.bukkit.Effect.EXPLOSION_HUGE, null);
                player.playSound(target.getLocation(), org.bukkit.Sound.EXPLODE, 1F, 1F);
            }
        }
        sender.sendMessage(ChatColor.GREEN + "You have exploded " + ChatColor.WHITE +  target.getName() + ChatColor.GREEN + "!");

        return true;
    }
}
