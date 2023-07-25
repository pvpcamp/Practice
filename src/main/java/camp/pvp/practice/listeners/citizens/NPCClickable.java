package camp.pvp.practice.listeners.citizens;

import camp.pvp.practice.Practice;
import org.bukkit.configuration.file.FileConfiguration;

public enum NPCClickable {
    UNRANKED, RANKED, HOST_EVENT, STATISTICS, LEADERBOARDS;

    public static NPCClickable getForId(int i) {
        FileConfiguration config = Practice.instance.getConfig();
        for(NPCClickable c : NPCClickable.values()) {
            if(config.isSet("npc_ids." + c.name().toLowerCase())) {
                int id = config.getInt("npc_ids." + c.name().toLowerCase());
                if(id == i) {
                    return c;
                }
            }
        }

        return null;
    }
}
