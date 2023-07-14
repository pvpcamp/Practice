package camp.pvp.arenas;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class Arena {

    public enum Type {
        DUEL, DUEL_BUILD, DUEL_SUMO, DUEL_HCF, DUEL_TEAMS, HCF_TEAMFIGHT, FFA, EVENT_SUMO, EVENT_SPLEEF, EVENT_OITC;

        public List<String> getValidPositions() {
            switch(this) {
                case DUEL_TEAMS:
                    return Arrays.asList("blue", "red", "yellow", "white", "center");
                case DUEL_BUILD:
                    return Arrays.asList("spawn1", "spawn2", "center", "corner1", "corner2");
                case FFA:
                    return Arrays.asList("spawn");
                default:
                    return Arrays.asList("spawn1", "spawn2", "center");
            }
        }
    }

    private @Getter @Setter String name, displayName;
    private @Getter @Setter Arena.Type type;
    private @Getter @Setter Map<String, ArenaPosition> positions;
    private @Getter @Setter boolean enabled, inUse;

    public Arena(String name) {
        this.name = name;
        this.displayName = name;
        this.type = Type.DUEL;
        this.positions = new HashMap<>();
    }
}
