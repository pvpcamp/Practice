package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.profiles.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PracticeUtilCommand implements CommandExecutor {

    private Practice plugin;
    public PracticeUtilCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("practiceutil").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            Location location = player.getLocation();
            if (args.length != 0) {
                switch(args[0].toLowerCase()) {
                    case "setlobby":
                        location.setY(location.getY() + 1);
                        plugin.setLobbyLocation(location);
                        player.sendMessage(ChatColor.GREEN + "The lobby has been set to your current location.");
                        return true;
                    case "setkiteditor":
                        location.setY(location.getY() + 1);
                        plugin.setKitEditorLocation(location);
                        player.sendMessage(ChatColor.GREEN + "The kit editor has been set to your current location.");
                        return true;
                    case "reset":
                        profile.playerUpdate();
                        return true;
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("&6&l/practiceutil &r&6Help");
            sb.append("\n&6/practiceutil setlobby &7- &fSets the lobby location.");
            sb.append("\n&6/practiceutil setkiteditor &7- &fSets the kit editor location.");
            sb.append("\n&6/practiceutil reset &7- &fResets your player.");
        }

        return true;
    }
}
