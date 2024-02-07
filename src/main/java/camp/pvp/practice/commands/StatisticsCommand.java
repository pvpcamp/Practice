package camp.pvp.practice.commands;

import camp.pvp.mongo.MongoCollectionResult;
import camp.pvp.practice.guis.statistics.StatisticsGui;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.profiles.stats.ProfileELO;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatisticsCommand implements CommandExecutor {

    private Practice plugin;
    public StatisticsCommand(Practice plugin) {
        this.plugin = plugin;
        plugin.getCommand("stats").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            String target = player.getName();

            if(args.length > 0) {
                target = args[0];
                if(!target.matches("^[a-zA-Z0-9_]{1,16}$")) {
                    return true;
                }

                target = target.replaceAll("\\$[A-Za-z0-9]+(_[A-Za-z0-9]+)*\\$", "");
            }

            player.sendMessage(ChatColor.GREEN + "Attempting to find statistics for player " + ChatColor.WHITE + target + ChatColor.GREEN + "...");

            GameProfileManager gpm = plugin.getGameProfileManager();

            final String fTarget = target;
            plugin.getGameProfileManager().getMongoManager().getCollection(true, gpm.getEloCollection(), new MongoCollectionResult() {
                @Override
                public void call(MongoCollection<Document> mongoCollection) {
                    ProfileELO[] elo = {null};
                    mongoCollection.find(Filters.regex("name","(?i)" + fTarget)).forEach(document -> {
                        if(document.getString("name").equalsIgnoreCase(fTarget)) {
                            elo[0] = new ProfileELO(document.get("_id", UUID.class));
                            elo[0].importFromDocument(document);
                        }
                    });

                    if(elo[0] != null) {
                        new StatisticsGui(elo[0]).open(player);
                    } else {
                        player.sendMessage(ChatColor.RED + "The player you specified has not played on this server.");
                    }
                }
            });
        }

        return true;
    }
}
