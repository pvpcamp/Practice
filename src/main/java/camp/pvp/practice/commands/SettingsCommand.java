package camp.pvp.practice.commands;

import camp.pvp.practice.guis.profile.SettingsGui;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand implements CommandExecutor {

    private Practice plugin;
    public SettingsCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("settings").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

            if(profile != null) {
                SettingsGui gui = new SettingsGui(profile);
                gui.open(player);
            }
        }

        return true;
    }
}
