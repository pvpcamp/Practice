package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.profiles.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerTimeCommand implements CommandExecutor {

    private Practice plugin;
    public PlayerTimeCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getCommand("playertime").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().find(player.getUniqueId(), true);

            switch(label.toLowerCase()) {
                case "sunrise":
                    profile.setTime(GameProfile.Time.SUNRISE);
                    player.setPlayerTime(GameProfile.Time.SUNRISE.getTime(), false);
                    player.sendMessage(ChatColor.GREEN + "Your time has been set to sunrise.");
                    break;
                case "day":
                    profile.setTime(GameProfile.Time.DAY);
                    player.setPlayerTime(GameProfile.Time.DAY.getTime(), false);
                    player.sendMessage(ChatColor.GREEN + "Your time has been set to day.");
                    break;
                case "sunset":
                    profile.setTime(GameProfile.Time.SUNSET);
                    player.setPlayerTime(GameProfile.Time.SUNSET.getTime(), false);
                    player.sendMessage(ChatColor.GREEN + "Your time has been set to sunset.");
                    break;
                case "night":
                    profile.setTime(GameProfile.Time.NIGHT);
                    player.setPlayerTime(GameProfile.Time.NIGHT.getTime(), false);
                    player.sendMessage(ChatColor.GREEN + "Your time has been set to night.");
                    break;
                default:
                    player.sendMessage(ChatColor.RED + "Available commands: /sunrise, /day, /sunset, /night");
            }
        }

        return true;
    }
}
