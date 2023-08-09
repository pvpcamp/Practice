package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter @Setter
public class Arena implements Comparable<Arena>{

    public enum Type {
        DUEL, DUEL_FLAT, DUEL_BUILD, DUEL_SUMO, DUEL_HCF, DUEL_SKYWARS, HCF_TEAMFIGHT, FFA, EVENT_SUMO, EVENT_SPLEEF, EVENT_OITC;

        public List<String> getValidPositions() {
            switch(this) {
                case DUEL_BUILD:
                case DUEL_SKYWARS:
                    return Arrays.asList("spawn1", "spawn2", "center", "corner1", "corner2");
                case EVENT_SUMO:
                    return Arrays.asList("spawn1", "spawn2", "lobby");
                case FFA:
                    return Arrays.asList("spawn");
                default:
                    return Arrays.asList("spawn1", "spawn2", "center");
            }
        }

        public boolean isGenerateLoot() {
            switch(this) {
                case DUEL_SKYWARS:
                    return true;
                default:
                    return false;
            }
        }

        public boolean canModifyArena() {
            switch(this) {
                case DUEL_SKYWARS:
                    return true;
                default:
                    return false;
            }
        }
    }

    private String name, displayName;
    private Arena.Type type;
    private Map<String, ArenaPosition> positions;
    private boolean enabled, inUse;
    private String parent;
    private int xDifference, zDifference;

    private @Getter List<Block> placedBlocks;
    private @Getter List<ModifiedBlock> modifiedBlocks;
    private @Getter BukkitTask replaceTask;

    public Arena(String name) {
        this.name = name;
        this.displayName = name;
        this.type = Type.DUEL;
        this.positions = new HashMap<>();

        this.placedBlocks = new ArrayList<>();
        this.modifiedBlocks = new ArrayList<>();
    }

    public boolean isCopy() {
        return this.getParent() != null;
    }

    public boolean hasValidPositions() {
        for(String position : getType().getValidPositions()) {
            if(getPositions().get(position) == null) {
                return false;
            }
        }

        return true;
    }

    public void copyPositions(Arena fromArena) {
        for(ArenaPosition position : fromArena.getPositions().values()) {
            Location location = position.getLocation();
            Location newLocation = location.clone();
            newLocation.add(xDifference, 0, zDifference);
            positions.put(position.getPosition(), new ArenaPosition(position.getPosition(), newLocation));
        }
    }

    public void resetArena()
    {
        if(getPlacedBlocks().size() > 0 || getModifiedBlocks().size() > 0) {
            Practice.instance.getArenaManager().getArenaResetter().addArena(this);
        }
    }

    @Override
    public int compareTo(Arena arena) {
        return this.getName().compareTo(arena.getName());
    }
}
