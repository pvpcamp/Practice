package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaPositionCommand implements CommandExecutor {

    private Practice plugin;
    public ArenaPositionCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("arenaposition").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) return true;

        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        Location location = player.getLocation();

        profile.setSelectedLocation(player.getLocation());

        player.sendMessage(ChatColor.GREEN + "Updated arena position selection to your current location. "
                + ChatColor.GRAY + "(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")" +
                "\n" + ChatColor.YELLOW + "Reminder: You can also use a Golden Axe to select the arena position.");

        return true;
    }
}
