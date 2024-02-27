package camp.pvp.practice.profiles.stats;

import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.queue.GameQueue;
import lombok.Data;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class ProfileStatistics {

    private final UUID uuid;
    private Map<GameKit, ProfileIndividualStatistics> unranked;
    private Map<GameKit, ProfileIndividualStatistics> ranked;
    private ProfileIndividualStatistics global;

    public ProfileStatistics(UUID uuid) {
        this.uuid = uuid;
        this.unranked = new HashMap<>();
        this.ranked = new HashMap<>();
        this.global = new ProfileIndividualStatistics();

        for (GameKit kit : GameKit.values()) {
            unranked.put(kit, new ProfileIndividualStatistics());

            if(kit.isRanked()) {
                ranked.put(kit, new ProfileIndividualStatistics());
            }
        }
    }

    public void incrementKills(GameKit kit, GameQueue.Type type) {
        switch(type) {
            case UNRANKED:
                unranked.get(kit).incrementKills();
                break;
            case RANKED:
                ranked.get(kit).incrementKills();
                break;
        }

        global.incrementKills();
    }

    public void incrementDeaths(GameKit kit, GameQueue.Type type) {
        switch(type) {
            case UNRANKED:
                unranked.get(kit).incrementDeaths();
                break;
            case RANKED:
                ranked.get(kit).incrementDeaths();
                break;
        }

        global.incrementDeaths();
    }

    public void incrementWins(GameKit kit, GameQueue.Type type) {
        switch(type) {
            case UNRANKED:
                unranked.get(kit).incrementWins();
                break;
            case RANKED:
                ranked.get(kit).incrementWins();
                break;
        }

        global.incrementWins();
    }

    public void resetWinStreak(GameKit kit, GameQueue.Type type) {
        switch(type) {
            case UNRANKED:
                unranked.get(kit).resetWinStreak();
                break;
            case RANKED:
                ranked.get(kit).resetWinStreak();
                break;
        }

        global.resetWinStreak();
    }

    public void importFromDocument(Document doc) {
        for (GameKit kit : GameKit.values()) {
            if (doc.containsKey("unranked_" + kit.name())) {
                Map<String, Object> map = (Map<String, Object>) doc.get("unranked_" + kit.name());
                ProfileIndividualStatistics stats = new ProfileIndividualStatistics();
                stats.importFromMap(map);
                unranked.put(kit, stats);
            }

            if (doc.containsKey("ranked_" + kit.name())) {
                Map<String, Object> map = (Map<String, Object>) doc.get("ranked_" + kit.name());
                ProfileIndividualStatistics stats = new ProfileIndividualStatistics();
                stats.importFromMap(map);
                ranked.put(kit, stats);
            }
        }

        ProfileIndividualStatistics global = new ProfileIndividualStatistics();
        global.importFromMap((Map<String, Object>) doc.get("global"));
        this.global = global;
    }

    public Map<String, Object> export() {
        Map<String, Object> map = new HashMap<>();
        for (GameKit kit : GameKit.values()) {
            map.put("unranked_" + kit.name(), unranked.get(kit).export());

            if(kit.isRanked()) {
                map.put("ranked_" + kit.name(), ranked.get(kit).export());
            }
        }

        map.put("global", global.export());
        return map;
    }
}
