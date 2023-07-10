package camp.pvp.commands;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.profiles.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommand implements CommandExecutor {

    private Practice plugin;
    public SpectateCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getCommand("spectate").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            if(args.length > 0) {
                if(!profile.getState().equals(GameProfile.State.LOBBY)) {
                    player.sendMessage(ChatColor.RED + "You cannot spectate a game right now.");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    GameProfile targetProfile = plugin.getGameProfileManager().getLoadedProfiles().get(target.getUniqueId());
                    Game game = targetProfile.getGame();
                    if(game == null) {
                        player.sendMessage(ChatColor.RED + "The player you specified is not in a game.");
                        return true;
                    }

                    game.spectateStart(player, target.getLocation());
                } else {
                    player.sendMessage(ChatColor.RED + "The player you specified was not found.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /" + label + " <player>");
            }
        }

        return true;
    }
}
