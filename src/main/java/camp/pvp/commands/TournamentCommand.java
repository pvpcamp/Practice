package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.Colors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class TournamentCommand implements CommandExecutor {

    private Practice plugin;
    public TournamentCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("tournament").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

            StringBuilder help = new StringBuilder();
            help.append("&6&l/tournament &r&6Help");
            help.append("\n&6/tournament join &7- &fJoin the tournament.");
            help.append("\n&6/tournament leave &7- &fLeave the tournament.");
            help.append("\n&6/tournament host &7- &fHost a tournament.");
            help.append("\n&6/tournament status &7- &fView the current tournament status.");
            player.sendMessage(Colors.get(help.toString()));
        }


        return true;
    }
}
