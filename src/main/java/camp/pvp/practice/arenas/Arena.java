package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.loot.LootChest;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.material.Bed;
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

        public boolean isBuild() {
            switch(this) {
                case DUEL_SKYWARS:
                case DUEL_BUILD:
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

    private @Getter List<Location> beds, chests;
    private @Getter List<ModifiedBlock> placedBlocks, modifiedBlocks;
    private @Getter BukkitTask replaceTask;

    public Arena(String name) {
        this.name = name;
        this.displayName = name;
        this.type = Type.DUEL;
        this.positions = new HashMap<>();

        this.beds = new ArrayList<>();
        this.chests = new ArrayList<>();
        this.placedBlocks = new ArrayList<>();
        this.modifiedBlocks = new ArrayList<>();
    }

    /**
     * Returns true if this arena is a copy of another arena.
     */
    public boolean isCopy() {
        return this.getParent() != null;
    }

    /**
     * Checks if all required positions for the arena set type exist.
     */
    public boolean hasValidPositions() {
        for(String position : getType().getValidPositions()) {
            if(getPositions().get(position) == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Copies positions from the parent arena to this arena, based on X and Z differences.
     * @param fromArena Parent arena.
     */

    public void copyPositions(Arena fromArena) {
        for(ArenaPosition position : fromArena.getPositions().values()) {
            Location location = position.getLocation();
            Location newLocation = location.clone();
            newLocation.add(xDifference, 0, zDifference);
            positions.put(position.getPosition(), new ArenaPosition(position.getPosition(), newLocation));
        }
    }

    /**
     * Prepares an arena before a match starts. Typically only useful for build enabled arenas.
     */
    public void prepare() {
        if(getType().isBuild()) {
            setInUse(true);
        }

        if(getType().isGenerateLoot()) {
            LootChest.generateLoot(this);
        }

    }

    /**
     * Adds the arena to the ArenaResetter queue if any blocks were modified.
     */
    public void resetArena() {
        if(!getPlacedBlocks().isEmpty() || !getModifiedBlocks().isEmpty()) {
            Practice.instance.getArenaManager().getArenaResetter().addArena(this);
        } else {
            setInUse(false);
        }
    }

    public void addPlacedBlock(ModifiedBlock modifiedBlock) {
        boolean add = true;
        for(ModifiedBlock b : placedBlocks) {
            if(modifiedBlock.getLocation().equals(b.getLocation())) {
                add = false;
                break;
            }
        }

        if(add) {
            placedBlocks.add(modifiedBlock);
        }
    }

    public void addModifiedBlock(ModifiedBlock modifiedBlock) {
        boolean add = true;
        for(ModifiedBlock b : modifiedBlocks) {
            if(modifiedBlock.getLocation().equals(b.getLocation())) {
                add = false;
                break;
            }
        }

        if(add) {
            placedBlocks.add(modifiedBlock);
        }
    }

    @Override
    public int compareTo(Arena arena) {
        return this.getName().compareTo(arena.getName());
    }
}
