package camp.pvp.practice.kits;

import camp.pvp.practice.Practice;

import java.util.HashMap;
import java.util.Map;

public class KitManager {

    private Practice plugin;
    private Map<NewGameKit, BaseKit> kits;

    public KitManager(Practice plugin) {
        this.plugin = plugin;
        this.kits = new HashMap<>();

        for(NewGameKit gameKit : NewGameKit.values()) {
            BaseKit kit = gameKit.getBaseKit();
            kits.put(gameKit, kit);
        }
    }

    public BaseKit getKit(NewGameKit gameKit) {
        return kits.get(gameKit);
    }
}
