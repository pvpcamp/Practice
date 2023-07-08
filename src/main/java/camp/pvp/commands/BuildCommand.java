package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.profiles.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCommand implements CommandExecutor {

    private Practice plugin;
    public BuildCommand(Practice plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginCommand("build").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {

            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

            profile.setBuildMode(!profile.isBuildMode());
            player.sendMessage(ChatColor.GREEN + "Build mode has been " + ChatColor.WHITE + (profile.isBuildMode() ? "enabled" : "disabled") + ChatColor.GREEN + ".");

        }

        return true;
    }
}
