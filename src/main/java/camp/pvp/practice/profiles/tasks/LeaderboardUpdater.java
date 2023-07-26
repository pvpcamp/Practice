package camp.pvp.practice.profiles.tasks;

import camp.pvp.mongo.MongoCollectionResult;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.GameProfileManager;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import org.bson.Document;

import java.util.*;

public class LeaderboardUpdater implements Runnable{

    private GameProfileManager gpm;
    private @Getter Map<DuelKit, List<LeaderboardEntry>> leaderboard;
    public LeaderboardUpdater(GameProfileManager gpm) {
        this.gpm = gpm;
        this.leaderboard = new HashMap<>();
    }

    @Override
    public void run() {
        gpm.getMongoManager().getCollection(true, gpm.getEloCollection(), new MongoCollectionResult() {
            @Override
            public void call(MongoCollection<Document> mongoCollection) {
                for(DuelKit kit : DuelKit.values()) {
                    if(kit.isQueueable() && kit.isRanked()) {
                        List<LeaderboardEntry> entries = new ArrayList<>();

                        mongoCollection.find().sort(new Document("kit_" + kit.name(), -1)).limit(10).forEach(
                                document -> entries.add(new LeaderboardEntry(document.getString("name"), document.getInteger("kit_" + kit.name())))
                        );

                        Collections.sort(entries);

                        leaderboard.put(kit, entries);
                    }
                }
            }
        });
    }
}
