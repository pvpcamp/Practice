package camp.pvp.practice.profiles.stats;

import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.queue.GameQueue;
import lombok.Data;
import org.bson.Document;

import java.util.*;

@Data
public class ProfileStatistics {

    private final UUID uuid;
    private String name;
    private Map<GameKit, ProfileIndividualStatistics> unranked;
    private Map<GameKit, ProfileIndividualStatistics> ranked;
    private ProfileIndividualStatistics global;

    public ProfileStatistics(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.unranked = new HashMap<>();
        this.ranked = new HashMap<>();
        this.global = new ProfileIndividualStatistics();

        for (GameKit kit : GameKit.values()) {
            unranked.put(kit, new ProfileIndividualStatistics());

            if(kit.getBaseKit().isRanked()) {
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

    public int getElo(GameKit kit) {
        return ranked.get(kit).getElo();
    }

    public int addElo(GameKit kit, int difference) {
        int elo = ranked.get(kit).getElo();
        int newElo = elo + difference;
        ranked.get(kit).setElo(newElo);
        return newElo;
    }

    public int subtractElo(GameKit kit, int difference) {
        int elo = ranked.get(kit).getElo();
        int newElo = elo - difference;
        ranked.get(kit).setElo(newElo);
        return newElo;
    }

    public void resetRankedElo() {
        for(ProfileIndividualStatistics stats : ranked.values()) {
            stats.setElo(1000);
        }
    }

    public int getGlobalElo() {
        List<Integer> elos = new ArrayList<>();
        for(ProfileIndividualStatistics stats : ranked.values()) {
            elos.add(stats.getElo());
        }

        double total = 0;
        for(int elo : elos) {
            total += elo;
        }

        return (int) Math.round(total / elos.size());
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
        this.name = doc.get("name", "Unknown");
    }

    public Map<String, Object> export() {
        Map<String, Object> map = new HashMap<>();
        for (GameKit kit : GameKit.values()) {
            map.put("unranked_" + kit.name(), unranked.get(kit).export());

            if(kit.getBaseKit().isRanked()) {
                map.put("ranked_" + kit.name(), ranked.get(kit).export());
            }
        }

        map.put("global", global.export());
        map.put("name", name);
        return map;
    }
}
