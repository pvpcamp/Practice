package camp.pvp.practice.profiles.leaderboard;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.BaseKit;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.queue.GameQueue;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;

public class LeaderboardUpdater implements Runnable{

    private GameProfileManager gpm;
    private @Getter Map<GameKit, List<LeaderboardEntry>> leaderboard;
    public LeaderboardUpdater(GameProfileManager gpm) {
        this.gpm = gpm;
        this.leaderboard = new HashMap<>();
    }

    @Override
    public void run() {

        if(Bukkit.getOnlinePlayers().isEmpty() && !leaderboard.isEmpty()) {
            return;
        }

        long start = System.currentTimeMillis();

        MongoCollection<Document> collection = Practice.getInstance().getGameProfileManager().getStatisticsCollection();

        for(GameKit kit : GameKit.values()) {
            BaseKit baseKit = kit.getBaseKit();
            if(baseKit.getGameTypes().contains(GameQueue.GameType.DUEL) && baseKit.isRanked()) {
                List<LeaderboardEntry> entries = new ArrayList<>();

                leaderboard.put(kit, entries);

                collection.find().sort(new Document("ranked_" + kit.name() + ".elo", -1)).limit(10).forEach(
                        document ->  {
                            String name = document.getString("name");
                            Document desc = (Document) document.get("ranked_" + kit.name());
                            int elo = desc.getInteger("elo");
                            entries.add(new LeaderboardEntry(name, elo));
                        }
                );

//                Collections.sort(entries);
            }
        }

        Practice.getInstance().sendDebugMessage("Leaderboards refreshed in " + (System.currentTimeMillis() - start) + "ms.");
    }
}
