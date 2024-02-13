package camp.pvp.practice.commands;

import camp.pvp.mongo.MongoCollectionResult;
import camp.pvp.practice.guis.statistics.StatisticsGui;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.profiles.stats.ProfileELO;
import camp.pvp.practice.profiles.stats.ProfileStatistics;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class StatisticsCommand implements CommandExecutor {

    private Practice plugin;
    public StatisticsCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getCommand("statistics").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            final Player opener = player;
            String target = args.length > 0 ? args[0] : player.getName();

            if(args.length > 0) {
                if(!target.matches("^[a-zA-Z0-9_]{1,16}$")) {
                    player.sendMessage(ChatColor.RED + "Invalid name.");
                    return true;
                }
            }

            GameProfileManager gpm = plugin.getGameProfileManager();

            CompletableFuture<GameProfile> future = gpm.findAsync(target);
            future.thenAccept(profile -> {
                if(profile == null) {
                    player.sendMessage(ChatColor.RED + "The player you specified has not played on this server.");
                    return;
                }

                ProfileELO elo = profile.getProfileElo();
                ProfileStatistics statistics = profile.getProfileStatistics();
                new StatisticsGui(opener, elo, statistics).open(player);
            });
        }

        return true;
    }
}
