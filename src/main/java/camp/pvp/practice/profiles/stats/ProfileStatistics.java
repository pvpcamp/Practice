package camp.pvp.practice.profiles.stats;

import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.queue.GameQueue;
import lombok.Data;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class ProfileStatistics {

    private final UUID uuid;
    private Map<DuelKit, ProfileIndividualStatistics> unranked;
    private Map<DuelKit, ProfileIndividualStatistics> ranked;
    private ProfileIndividualStatistics global;

    public ProfileStatistics(UUID uuid) {
        this.uuid = uuid;
        this.unranked = new HashMap<>();
        this.ranked = new HashMap<>();
        this.global = new ProfileIndividualStatistics();

        for (DuelKit kit : DuelKit.values()) {
            unranked.put(kit, new ProfileIndividualStatistics());

            if(kit.isRanked()) {
                ranked.put(kit, new ProfileIndividualStatistics());
            }
        }
    }

    public void incrementKills(DuelKit kit, GameQueue.Type type) {
        if (type == GameQueue.Type.UNRANKED) {
            unranked.get(kit).incrementKills();
        } else {
            ranked.get(kit).incrementKills();
        }

        global.incrementKills();
    }

    public void incrementDeaths(DuelKit kit, GameQueue.Type type) {
        if (type == GameQueue.Type.UNRANKED) {
            unranked.get(kit).incrementDeaths();
        } else {
            ranked.get(kit).incrementDeaths();
        }

        global.incrementDeaths();
    }

    public void incrementWins(DuelKit kit, GameQueue.Type type) {
        if (type == GameQueue.Type.UNRANKED) {
            unranked.get(kit).incrementWins();
        } else {
            ranked.get(kit).incrementWins();
        }

        global.incrementWins();
    }

    public void resetWinStreak(DuelKit kit, GameQueue.Type type) {
        if (type == GameQueue.Type.UNRANKED) {
            unranked.get(kit).resetWinStreak();
        } else {
            ranked.get(kit).resetWinStreak();
        }

        global.resetWinStreak();
    }

    public void importFromDocument(Document doc) {
        for (DuelKit kit : DuelKit.values()) {
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
        for (DuelKit kit : DuelKit.values()) {
            map.put("unranked_" + kit.name(), unranked.get(kit).export());

            if(kit.isRanked()) {
                map.put("ranked_" + kit.name(), ranked.get(kit).export());
            }
        }

        map.put("global", global.export());
        return map;
    }
}
