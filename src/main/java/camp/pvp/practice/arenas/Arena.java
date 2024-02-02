package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.loot.LootChest;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class Arena implements Comparable<Arena>{

    public enum Type {
        DUEL, DUEL_FLAT, DUEL_BUILD, DUEL_SUMO, DUEL_HCF, DUEL_SKYWARS, DUEL_BED_FIGHT, DUEL_BRIDGE, SPLEEF, HCF_TEAMFIGHT, FFA, EVENT_SUMO, EVENT_OITC;

        public List<String> getValidPositions() {
            switch(this) {
                case DUEL_BED_FIGHT:
                    return Arrays.asList("spawn1", "spawn2", "corner1", "corner2", "bluecorner1", "bluecorner2", "redcorner1", "redcorner2");
                case DUEL_BUILD:
                case DUEL_SKYWARS:
                case SPLEEF:
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
                case SPLEEF:
                    return true;
                default:
                    return false;
            }
        }

        public List<Material> getSpecificBlocks() {
            switch(this) {
                case SPLEEF:
                    return Collections.singletonList(Material.SNOW_BLOCK);
                default:
                    return null;
            }
        }

        public boolean isBuild() {
            switch(this) {
                case DUEL_SKYWARS:
                case DUEL_BUILD:
                case SPLEEF:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isUnloadChunks() {
            switch(this) {
                case DUEL_SKYWARS:
                case DUEL_BUILD:
                case SPLEEF:
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

    private @Getter List<Location> beds, blocks, chests;
    private @Getter Set<Chunk> chunks;
    private @Getter Queue<Chunk> chunkQueue;

    public Arena(String name) {
        this.name = name;
        this.displayName = name;
        this.type = Type.DUEL;
        this.positions = new HashMap<>();

        this.beds = new ArrayList<>();
        this.blocks = new ArrayList<>();
        this.chests = new ArrayList<>();
        this.chunks = new HashSet<>();
        this.chunkQueue = new LinkedList<>();
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

    public void scanArena() {

        ArenaPosition corner1 = getPositions().get("corner1");
        ArenaPosition corner2 = getPositions().get("corner2");

        if(corner1 != null && corner2 != null) {

            getBeds().clear();
            getChests().clear();
            getBlocks().clear();
            getChunks().clear();

            int minX, minY, minZ, maxX, maxY, maxZ;
            Location c1 = corner1.getLocation(), c2 = corner2.getLocation();
            minX = Math.min(c1.getBlockX(), c2.getBlockX());
            minY = Math.min(c1.getBlockY(), c2.getBlockY());
            minZ = Math.min(c1.getBlockZ(), c2.getBlockZ());
            maxX = Math.max(c1.getBlockX(), c2.getBlockX());
            maxY = Math.max(c1.getBlockY(), c2.getBlockY());
            maxZ = Math.max(c1.getBlockZ(), c2.getBlockZ());

            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    for (int z = minZ; z < maxZ; z++) {
                        Location location = new Location(c1.getWorld(), x, y, z);
                        Block block = location.getBlock();
                        if(!block.isEmpty()) {
                            switch(block.getType()) {
                                case BED_BLOCK:
                                    getBeds().add(block.getLocation());
                                    break;
                                case CHEST:
                                case TRAPPED_CHEST:
                                    getChests().add(block.getLocation());
                                default:
                                    getBlocks().add(location);
                            }
                        }

                        getChunks().add(location.getChunk());
                    }
                }
            }
        }
    }

    public void resetArena() {
        if(!getType().isUnloadChunks()) return; // We don't need to reset the arena if we don't need to unload chunks.

        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), ()-> {
            if(getType().isUnloadChunks()) {
                for(Chunk chunk : getChunks()) {

                    for(Entity entity : chunk.getEntities()) {
                        if(!(entity instanceof Player)) continue;

                        Player player = (Player) entity;
                        player.kickPlayer(ChatColor.RED + "You were kicked because the arena you were in was reset.");
                    }

                    chunk.unload(false);
                }
            }

            setInUse(false);
        }, 5L);
    }

    public boolean isOriginalBlock(Location location) {
        return blocks.contains(location);
    }

    @Override
    public int compareTo(Arena arena) {
        return this.getName().compareTo(arena.getName());
    }
}
