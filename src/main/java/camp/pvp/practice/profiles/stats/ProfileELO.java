package camp.pvp.practice.profiles.stats;

import camp.pvp.practice.kits.GameKit;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class ProfileELO {

    private final UUID uuid;
    private String name;
    private Map<GameKit, Integer> ratings;


    public ProfileELO(UUID uuid) {
        this.uuid = uuid;
        this.ratings = new HashMap<>();

        resetRatings();
    }

    public void importFromDocument(Document doc) {
        this.name = doc.getString("name");

        for(GameKit kit : GameKit.values()) {
            if(doc.get("kit_" + kit.name()) != null) {
                ratings.put(kit, doc.getInteger("kit_" + kit.name()));
            }
        }
    }

    public int setElo(GameKit kit, int elo) {
        return ratings.put(kit, elo);
    }

    public int getElo(GameKit kit) {
        return ratings.get(kit);
    }

    public void addElo(GameKit kit, int difference) {
        final int current = ratings.get(kit);
        ratings.put(kit, current + difference);
    }

    public void subtractElo(GameKit kit, int difference) {
        final int current = ratings.get(kit);
        ratings.put(kit, current - difference);
    }

    public void resetRatings() {
        for(GameKit kit : GameKit.values()) {
            if(kit.getBaseKit().isRanked()) {
                ratings.put(kit, 1000);
            }
        }
    }

    public Map<String, Object> export() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);

        for(Map.Entry<GameKit, Integer> entry : ratings.entrySet()) {
            map.put("kit_" + entry.getKey().name(), entry.getValue());
        }

        return map;
    }
}
