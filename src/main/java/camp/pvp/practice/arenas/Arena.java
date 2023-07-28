package camp.pvp.practice.arenas;

import com.sk89q.worldedit.WorldEdit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.*;

public class Arena implements Comparable<Arena>{

    public enum Type {
        DUEL, DUEL_FLAT, DUEL_BUILD, DUEL_SUMO, DUEL_HCF, HCF_TEAMFIGHT, FFA, EVENT_SUMO, EVENT_SPLEEF, EVENT_OITC;

        public List<String> getValidPositions() {
            switch(this) {
                case DUEL_BUILD:
                    return Arrays.asList("spawn1", "spawn2", "center", "corner1", "corner2");
                case EVENT_SUMO:
                    return Arrays.asList("spawn1", "spawn2", "lobby");
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
    private @Getter @Setter boolean enabled, inUse, ranked;

    private @Getter @Setter String parent;
    private @Getter @Setter List<String> copies;

    public Arena(String name) {
        this.name = name;
        this.displayName = name;
        this.type = Type.DUEL;
        this.positions = new HashMap<>();
        this.copies = new ArrayList<>();
    }

    public boolean isCopy() {
        return this.getParent() != null;
    }

    @Override
    public int compareTo(Arena arena) {
        return this.getName().compareTo(arena.getName());
    }
}
