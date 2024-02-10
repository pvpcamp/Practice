package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.loot.LootChest;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.*;

@Getter @Setter
public class Arena implements Comparable<Arena>{

    private String name, displayName;
    private Arena.Type type;
    private Map<String, ArenaPosition> positions;
    private boolean enabled, inUse;
    private String parent;
    private Set<ChunkSnapshot> chunkSnapshots;
    // X and Z differences are the amount of chunks away from the parent arena.
    private int xDifference, zDifference, buildLimit, voidLevel;

    private @Getter List<Location> beds, blocks, chests;
    private @Getter Set<Chunk> chunks;

    public Arena(String name) {
        this.name = name;
        this.displayName = name;
        this.type = Type.DUEL;
        this.positions = new HashMap<>();

        this.beds = new ArrayList<>();
        this.blocks = new ArrayList<>();
        this.chests = new ArrayList<>();
        this.chunks = new HashSet<>();
        this.chunkSnapshots = new HashSet<>();

        this.buildLimit = 256;
        this.voidLevel = 0;
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
     */

    public void updateCopy(boolean reset) {

        Arena parent = Practice.getInstance().getArenaManager().getArenaFromName(getParent());

        for(ArenaPosition position : parent.getPositions().values()) {
            Location location = position.getLocation();
            Location newLocation = location.clone();
            newLocation.add(xDifference * 16, 0, zDifference * 16);
            positions.put(position.getPosition(), new ArenaPosition(position.getPosition(), newLocation));
        }

        setEnabled(parent.isEnabled());
        setDisplayName(parent.getDisplayName());
        setBuildLimit(parent.getBuildLimit());
        setVoidLevel(parent.getVoidLevel());

        scanArena();

        if(reset) {
            resetArena();
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
                            }

                            getBlocks().add(location);
                        }

                        getChunks().add(location.getChunk());
                    }
                }
            }

            if(!isCopy()) {
                for (Chunk chunk : getChunks()) {
                    chunkSnapshots.add(chunk.getChunkSnapshot());
                }
            }
        }
    }

    public void clearBlocks() {
        scanArena();
        for(Location location : getBlocks()) {
            location.getBlock().setType(Material.AIR);
        }
    }

    /***
     * Resets the arena.
     */
    public void resetArena() {
        if(!getType().isResetAfterGame()) return;
        if(!isCopy()) return;

        Bukkit.getScheduler().runTask(Practice.getInstance(), ()-> {
            List<ChunkSnapshot> snapshots = new ArrayList<>();
            for(Chunk chunk : getChunks()) {
                snapshots.add(chunk.getChunkSnapshot());
            }

            for(ChunkSnapshot snapshot : snapshots) {

                ChunkSnapshot beforeSnapshot = null;
                for(ChunkSnapshot before : Practice.getInstance().getArenaManager().getArenaFromName(getParent()).getChunkSnapshots()) {

                    if(snapshot.getX() == before.getX() + xDifference && snapshot.getZ() == before.getZ() + zDifference) {
                        beforeSnapshot = before;
                        break;
                    }
                }

                if(beforeSnapshot == null) continue;

                for(int x = 0; x < 16; x++) {
                    for(int z = 0; z < 16; z++) {
                        for(int y = 0; y < 256; y++) {
                            int type = beforeSnapshot.getBlockTypeId(x, y, z);
                            int data = beforeSnapshot.getBlockData(x, y, z);
                            boolean blockChanged = snapshot.getBlockTypeId(x, y, z) != type;
                            if(!blockChanged) {
                                blockChanged = snapshot.getBlockData(x, y, z) != data;
                            }

                            if(blockChanged) {
                                Location location = new Location(Bukkit.getWorld(snapshot.getWorldName()), snapshot.getX() * 16 + x, y, snapshot.getZ() * 16 + z);
                                RestoreBlock block = new RestoreBlock(location, type, data);
                                block.restore();
                            }
                        }
                    }
                }
            }

            inUse = false;
        });
    }

    public boolean isOriginalBlock(Location location) {

        if(!type.equals(Type.DUEL_BED_FIGHT)) return blocks.contains(location);

        for(ArenaPosition position : positions.values()) {
            if(position.getPosition().equalsIgnoreCase("bluebed") || position.getPosition().equalsIgnoreCase("redbed")) {
                Location l = position.getLocation();

                for(int x = l.getBlockX() - 3; x < l.getBlockX() + 3; x++) {
                    for(int y = l.getBlockY(); y < l.getBlockY() + 3; y++) {
                        for(int z = l.getBlockZ() - 3; z < l.getBlockZ() + 3; z++) {
                            Location blockLocation = new Location(l.getWorld(), x, y, z);
                            Block block = blockLocation.getBlock();
                            if(block.equals(location.getBlock())) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return blocks.contains(location);
    }

    @Override
    public int compareTo(Arena arena) {
        return this.getName().compareTo(arena.getName());
    }

    public enum Type {
        DUEL, DUEL_FLAT, DUEL_BUILD, DUEL_SUMO, DUEL_HCF, DUEL_SKYWARS, DUEL_BED_FIGHT, DUEL_BRIDGE, SPLEEF, HCF_TEAMFIGHT, FFA, EVENT_SUMO, EVENT_OITC;

        public List<String> getValidPositions() {
            switch(this) {
                case DUEL_BED_FIGHT:
                    return Arrays.asList("spawn1", "spawn2", "corner1", "corner2", "bluebed", "redbed");
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
                case DUEL_BED_FIGHT:
                case SPLEEF:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isResetAfterGame() {
            switch(this) {
                case DUEL_SKYWARS:
                case DUEL_BUILD:
                case DUEL_BED_FIGHT:
                case SPLEEF:
                    return true;
                default:
                    return false;
            }
        }

        public Material getGuiMaterial() {
            switch(this) {
                case DUEL: return Material.GRASS;
                case DUEL_FLAT: return Material.WOOD_PLATE;
                case DUEL_BUILD: return Material.DIAMOND_PICKAXE;
                case DUEL_HCF: return Material.FENCE;
                case DUEL_SUMO: return Material.LEASH;
                case DUEL_SKYWARS: return Material.EYE_OF_ENDER;
                case DUEL_BED_FIGHT: return Material.BED;
                case DUEL_BRIDGE: return Material.STAINED_CLAY;
                case SPLEEF: return Material.SNOW_BLOCK;
                case HCF_TEAMFIGHT: return Material.DIAMOND_SWORD;
                case FFA: return Material.DIAMOND;
                case EVENT_SUMO: return Material.SLIME_BALL;
                case EVENT_OITC: return Material.ARROW;
                default: return Material.STONE;
            }
        }

        @Override
        public String toString() {
            String name = this.name();
            name = name.replace("_", " ");
            return WordUtils.capitalizeFully(name);
        }
    }
}
