package camp.pvp.practice.commands;

import camp.pvp.practice.profiles.DuelRequest;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand implements CommandExecutor {

    private Practice plugin;
    public AcceptCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("accept").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player && args.length > 0) {
            Player player = (Player) sender;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

            if(profile.getState().equals(GameProfile.State.LOBBY)) {

                Player target = Bukkit.getPlayer(args[0]);

                if (target != null && target != player) {

                    GameProfile targetProfile = plugin.getGameProfileManager().getLoadedProfiles().get(target.getUniqueId());

                    if (targetProfile.getGame() == null) {
                        DuelRequest duelRequest = profile.getDuelRequests().get(targetProfile.getUuid());
                        if(duelRequest != null) {
                            if (duelRequest.isExpired()) {
                                player.sendMessage(ChatColor.RED + "This duel request has expired.");
                                return true;
                            }

                            duelRequest.startGame();
                        } else {
                            player.sendMessage(ChatColor.RED + "You do not have a duel request from this player.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "The player you specified is in a game.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "The player you specified is not on this server.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You cannot duel someone right now.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /accept <player>");
        }

        return true;
    }
}
