package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhereAmICommand implements CommandExecutor {

    private Practice plugin;
    public WhereAmICommand(Practice plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginCommand("whereami").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

            StringBuilder sb = new StringBuilder();
            sb.append("&6&lWhere Am I?");

            Game game = profile.getGame();

            if(game != null) {
                sb.append("\n&6Arena: &f" + game.getArena().getDisplayName() + " &7(" + game.getArena().getName());
                sb.append("\n&6Currently playing: &f" + game.getCurrentPlaying().containsKey(player.getUniqueId()));
            } else {
                sb.append("\n&6&oYou are not currently in game.");
            }

            sb.append("\n&6State: &f" + profile.getState().toString());

            player.sendMessage(Colors.get(sb.toString()));
        }

        return true;
    }
}
