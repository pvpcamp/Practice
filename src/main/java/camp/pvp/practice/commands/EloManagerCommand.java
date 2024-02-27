package camp.pvp.practice.commands;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.stats.ProfileELO;
import camp.pvp.practice.utils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

public class EloManagerCommand implements CommandExecutor {

    private Practice plugin;
    public EloManagerCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("elomanager").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length > 0) {
            switch(args[0].toLowerCase()) {
                case "reset" -> {
                    if(args.length > 1) {
                        reset(sender, args[1]);
                        return true;
                    }
                }
                case "ladder" -> {
                    if(args.length > 3) {
                        int elo;
                        try {
                            elo = Integer.parseInt(args[3]);
                        } catch (NumberFormatException ignored) {
                            sender.sendMessage(ChatColor.RED + "Invalid ELO.");
                            return true;
                        }

                        ladder(sender, args[1], args[2], elo);
                        return true;
                    }
                }
            }
        }

        StringBuilder help = new StringBuilder();
        help.append("&6&l/elomanager &r&6Help");
        help.append("\n&6/elomanager reset <player> &7- &fResets all ELO for player for all kits.");
        help.append("\n&6/elomanager ladder <player> <kit> <elo> &7- &fSets ELO for player for specified kit.");

        sender.sendMessage(Colors.get(help.toString()));

        return true;
    }

    public void reset(CommandSender sender, String target) {
        CompletableFuture<GameProfile> profileFuture = plugin.getGameProfileManager().findAsync(target);
        profileFuture.thenAccept(profile -> {
            if(profile == null) {
                sender.sendMessage(ChatColor.RED + "The player you specified has never joined the network.");
                return;
            }

            ProfileELO profileELO = profile.getProfileElo();
            profileELO.resetRatings();
            plugin.getGameProfileManager().exportElo(profileELO);
            sender.sendMessage(ChatColor.GREEN + "ELO for player " + ChatColor.WHITE + profile.getName() + ChatColor.GREEN + " has been reset.");
        });
    }

    public void ladder(CommandSender sender, String target, String kit, int elo) {
        CompletableFuture<GameProfile> profileFuture = plugin.getGameProfileManager().findAsync(target);
        profileFuture.thenAccept(profile -> {
            if(profile == null) {
                sender.sendMessage(ChatColor.RED + "The player you specified has never joined the network.");
                return;
            }

            GameKit gameKit = null;
            for(GameKit k : GameKit.values()) {
                if(k.name().equalsIgnoreCase(kit)) {
                    gameKit = k;
                }
            }

            if(gameKit == null) {
                sender.sendMessage(ChatColor.RED + "The kit you specified does not exist.");
                return;
            }

            ProfileELO profileELO = profile.getProfileElo();

            profileELO.setElo(gameKit, elo);
            plugin.getGameProfileManager().exportElo(profileELO);
            sender.sendMessage(ChatColor.GREEN + "ELO for player " + ChatColor.WHITE + profile.getName() + ChatColor.GREEN + " for kit " + ChatColor.WHITE + gameKit.getDisplayName() + ChatColor.GREEN + " has been set to " + elo + ".");
        });
    }
}
