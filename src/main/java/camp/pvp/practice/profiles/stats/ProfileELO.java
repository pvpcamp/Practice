package camp.pvp.practice.profiles.stats;

import camp.pvp.practice.kits.DuelKit;
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
    private Map<DuelKit, Integer> ratings;


    public ProfileELO(UUID uuid) {
        this.uuid = uuid;
        this.ratings = new HashMap<>();

        for(DuelKit kit : DuelKit.values()) {
            if(kit.isRanked()) {
                ratings.put(kit, 1000);
            }
        }
    }

    public void importFromDocument(Document doc) {
        this.name = doc.getString("name");

        for(DuelKit kit : DuelKit.values()) {
            if(doc.get("kit_" + kit.name()) != null) {
                ratings.put(kit, doc.getInteger("kit_" + kit.name()));
            }
        }
    }

    public Map<String, Object> export() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);

        for(Map.Entry<DuelKit, Integer> entry : ratings.entrySet()) {
            map.put("kit_" + entry.getKey().name(), entry.getValue());
        }

        return map;
    }
}
