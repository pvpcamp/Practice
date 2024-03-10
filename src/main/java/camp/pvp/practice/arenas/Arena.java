package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import xyz.refinedev.spigot.api.chunk.ChunkAPI;
import xyz.refinedev.spigot.api.chunk.ChunkSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.logging.Logger;

@Getter @Setter
public class Arena implements Comparable<Arena>{

    private String name, displayName;
    private Arena.Type type;
    private UUID worldId;
    private Map<String, ArenaPosition> positions;
    private List<Location> randomSpawnLocations;
    private List<LootChest> lootChests;

    private boolean enabled, inUse;
    private String parentName;
    private int xDifference, zDifference, buildLimit, voidLevel;

    private @Getter List<Location> solidBlocks;
    private @Getter List<StoredChunk> storedChunks;
    private @Getter Map<StoredChunk, ChunkSnapshot> snapshots;

    public Arena(String name) {
        this.name = name;
        this.displayName = name;
        this.type = Type.DUEL;
        this.positions = new HashMap<>();
        this.randomSpawnLocations = new ArrayList<>();
        this.lootChests = new ArrayList<>();

        this.solidBlocks = new ArrayList<>();
        this.snapshots = new HashMap<>();
        this.storedChunks = new ArrayList<>();

        this.buildLimit = 256;
        this.voidLevel = 0;
    }

    /**
     * Returns true if this arena is a copy of another arena.
     */
    public boolean isCopy() {
        return this.getParentName() != null;
    }

    public Arena getParent() {
        return getParentName() == null ? null : Practice.getInstance().getArenaManager().getArenaFromName(getParentName());
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

        if(getType().isGenerateLoot() && lootChests.isEmpty()) return false;
        if(getType().isRandomSpawnLocation() && randomSpawnLocations.isEmpty()) return false;

        return true;
    }

    /**
     * Copies positions from the parent arena to this arena, based on X and Z differences.
     */
    public void updateCopy(Arena parent, boolean reset) {

        if(!isCopy()) return;

        positions.clear();
        lootChests.clear();
        randomSpawnLocations.clear();

        for(ArenaPosition position : parent.getPositions().values()) {
            Location location = position.getLocation();
            Location newLocation = location.clone();
            newLocation.add(xDifference, 0, zDifference);
            positions.put(position.getPosition(), new ArenaPosition(position.getPosition(), newLocation));
        }

        for(LootChest lootChest : parent.getLootChests()) {
            Location location = lootChest.getLocation();
            Location newLocation = location.clone();
            newLocation.add(xDifference, 0, zDifference);
            lootChests.add(new LootChest(newLocation, lootChest.getLootCategory()
            ));
        }

        setEnabled(parent.isEnabled());
        setDisplayName(parent.getDisplayName());
        setBuildLimit(parent.getBuildLimit());
        setVoidLevel(parent.getVoidLevel());

        if(reset) {
            copyBlocks();
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

            for(LootChest lootChest : lootChests) {
                Location l = lootChest.getLocation();
                Block block = l.getBlock();
                BlockState state = block.getState();

                block.setType(Material.CHEST);
                state.setType(Material.CHEST);
                state.update(true, false);

                if(l.getBlock().getState() instanceof Chest chest) {
                    chest.getInventory().clear();
                }
            }

            for(LootChest lootChest : lootChests) {
                lootChest.generateLoot(lootChests);
            }
        }
    }

    public void scanArena() {

        ArenaPosition corner1 = getPositions().get("corner1");
        ArenaPosition corner2 = getPositions().get("corner2");

        if(corner1 != null && corner2 != null) {

            getSnapshots().clear();
            getSolidBlocks().clear();

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
                            solidBlocks.add(location);
                        }

                        if(worldId == null) {
                            worldId = location.getWorld().getUID();
                        }
                    }
                }
            }

            generateStoredChunks();
            refreshChunkSnapshots();
        }
    }

    public void refreshChunkSnapshots() {
        getSnapshots().clear();

        ChunkAPI api = ChunkAPI.getInstance();

        Practice.getInstance().getLogger().info("[Arena#refreshChunkSnapshots] Refreshing " + storedChunks.size() + " chunk snapshots for " + getName() + ".");

        for(StoredChunk sc : storedChunks) {
            getSnapshots().put(sc, api.takeSnapshot(sc.getWorld().getChunkAt(sc.getX(), sc.getZ())));
        }
    }

    public void clearBlocks() {
        scanArena();
        for(Location location : getSolidBlocks()) {
            location.getBlock().setType(Material.AIR);
        }
    }

    public void generateStoredChunks() {
        if(!hasValidPositions()) return;

        storedChunks.clear();

        int minX, minZ, maxX, maxZ;
        Location c1 = positions.get("corner1").getLocation(), c2 = positions.get("corner2").getLocation();
        minX = Math.min(c1.getBlockX(), c2.getBlockX());
        minZ = Math.min(c1.getBlockZ(), c2.getBlockZ());
        maxX = Math.max(c1.getBlockX(), c2.getBlockX());
        maxZ = Math.max(c1.getBlockZ(), c2.getBlockZ());

        int chunkMinX = Integer.MAX_VALUE, chunkMinZ = Integer.MAX_VALUE, chunkMaxX = Integer.MIN_VALUE, chunkMaxZ = Integer.MIN_VALUE;

        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                Location location = new Location(c1.getWorld(), x, 0, z);
                Chunk chunk = location.getChunk();

                int chunkX = chunk.getX(), chunkZ = chunk.getZ();

                if(chunkX < chunkMinX) {
                    chunkMinX = chunkX;
                }

                if(chunkX > chunkMaxX) {
                    chunkMaxX = chunkX;
                }

                if(chunkZ < chunkMinZ) {
                    chunkMinZ = chunkZ;
                }

                if(chunkZ > chunkMaxZ) {
                    chunkMaxZ = chunkZ;
                }
            }
        }

        chunkMinX -= 1;
        chunkMinZ -= 1;
        chunkMaxX += 1;
        chunkMaxZ += 1;

        for(int x = chunkMinX; x <= chunkMaxX; x++) {
            for(int z = chunkMinZ; z <= chunkMaxZ; z++) {
                storedChunks.add(new StoredChunk(x, z, worldId));
            }
        }
    }

    /***
     * Resets the arena.
     */
    public void resetArena() {
        if (!getType().isBuild()) return;
        if (!isCopy()) return;

        setInUse(true);

        ChunkAPI api = ChunkAPI.getInstance();

        for (Map.Entry<StoredChunk, ChunkSnapshot> entry : getSnapshots().entrySet()) {
            StoredChunk sc = entry.getKey();
            api.restoreSnapshot(sc.getBukkitChunk(), entry.getValue());
        }

        refreshChunkSnapshots();

        setInUse(false);
    }

    public void copyBlocks() {
        if (!getType().isBuild()) return;
        if (!isCopy()) return;

        setInUse(true);

        Logger logger = Practice.getInstance().getLogger();

        logger.info("[Arena#copyBlocks] Starting arena copy process for arena " + getName() + ".");

        int minX, minY, minZ, maxX, maxY, maxZ;
        Location c1 = getPositions().get("corner1").getLocation(), c2 = getPositions().get("corner2").getLocation();
        minX = Math.min(c1.getBlockX(), c2.getBlockX());
        minY = Math.min(c1.getBlockY(), c2.getBlockY());
        minZ = Math.min(c1.getBlockZ(), c2.getBlockZ());
        maxX = Math.max(c1.getBlockX(), c2.getBlockX());
        maxY = Math.max(c1.getBlockY(), c2.getBlockY());
        maxZ = Math.max(c1.getBlockZ(), c2.getBlockZ());

        List<Location> solidBlocks = new ArrayList<>(), airBlocks = new ArrayList<>();
        List<StoredChunk> newStoredChunks = new ArrayList<>();

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Location location = new Location(c1.getWorld(), x, y, z);
                    Block block = location.getBlock();

                    if (block.isEmpty()) {
                        airBlocks.add(location);
                    } else {
                        solidBlocks.add(location);
                    }

                    Chunk chunk = block.getChunk();
                    StoredChunk storedChunk = new StoredChunk(chunk.getX(), chunk.getZ(), worldId);
                    if(!newStoredChunks.contains(storedChunk)) {
                        newStoredChunks.add(storedChunk);
                    }
                }
            }
        }

        logger.info("[Arena#copyBlocks] Captured " + (solidBlocks.size() + airBlocks.size()) + " blocks and " + newStoredChunks.size() + " chunks for " + getName() + ", starting iteration.");

        List<Location> newSolidBlocks = new ArrayList<>();
        int changedBlocks = 0;

        for(Location location : solidBlocks) {
            Block block = location.getBlock();
            Location parentLocation = location.clone().subtract(xDifference, 0, zDifference);
            Block parentBlock = parentLocation.getBlock();

            if(parentBlock.isEmpty()) {
                block.setType(Material.AIR);

                changedBlocks++;
            } else {
                newSolidBlocks.add(location);
                if(!parentBlock.getType().equals(block.getType())) {
                    block.setType(parentBlock.getType());

                    BlockState parentState = parentBlock.getState();
                    BlockState newState = block.getState();
                    newState.setType(parentState.getType());
                    newState.setData(parentState.getData());
                    newState.update();

                    changedBlocks++;
                }
            }
        }

        for(Location location : airBlocks) {
            Block block = location.getBlock();
            Location parentLocation = location.clone().subtract(xDifference, 0, zDifference);
            Block parentBlock = parentLocation.getBlock();

            if(!parentBlock.isEmpty()) {
                newSolidBlocks.add(location);
                block.setType(parentBlock.getType());

                BlockState parentState = parentBlock.getState();
                BlockState newState = block.getState();
                newState.setType(parentState.getType());
                newState.setData(parentState.getData());
                newState.update();

                changedBlocks++;
            }
        }

        logger.info("[Arena#copyBlocks] Finished copying " + changedBlocks + " blocks for " + getName() + ".");

        setSolidBlocks(newSolidBlocks);
        setStoredChunks(newStoredChunks);

        refreshChunkSnapshots();

        setInUse(false);
    }

    public boolean isOriginalBlock(Location location) {

        if (!type.isBedRespawn()) return solidBlocks.contains(location);

        for (ArenaPosition position : positions.values()) {
            if(!position.getPosition().contains("bed")) continue;

            Location l = position.getLocation();

            for (int x = l.getBlockX() - 4; x < l.getBlockX() + 4; x++) {
                for (int y = l.getBlockY(); y < l.getBlockY() + 3; y++) {
                    for (int z = l.getBlockZ() - 4; z < l.getBlockZ() + 4; z++) {
                        Location blockLocation = new Location(l.getWorld(), x, y, z);
                        Block b = blockLocation.getBlock();
                        if (b.equals(location.getBlock())) {
                            return false;
                        }
                    }
                }
            }
        }

        return solidBlocks.contains(location);
    }

    @Override
    public int compareTo(Arena arena) {
        return this.getName().compareTo(arena.getName());
    }

    public enum Type {
        DUEL, DUEL_FLAT, DUEL_BUILD, DUEL_SUMO, DUEL_HCF, DUEL_SKYWARS, DUEL_BED_FIGHT, DUEL_FIREBALL_FIGHT, DUEL_BRIDGE, SPLEEF, HCF_TEAMFIGHT, FFA, EVENT_SUMO,
        MINIGAME_FIREBALL_BLITZ, MINIGAME_SKYWARS, MINIGAME_OITC;

        public List<String> getValidPositions() {
            return switch (this) {
                case DUEL_BED_FIGHT, DUEL_FIREBALL_FIGHT -> Arrays.asList("spawn1", "spawn2", "corner1", "corner2", "bluebed", "redbed");
                case DUEL_BUILD, DUEL_SKYWARS, SPLEEF -> Arrays.asList("spawn1", "spawn2", "center", "corner1", "corner2");
                case EVENT_SUMO -> Arrays.asList("spawn1", "spawn2", "lobby");
                case FFA -> Arrays.asList("spawn");
                case MINIGAME_FIREBALL_BLITZ -> Arrays.asList("bluespawn", "redspawn", "yellowspawn", "whitespawn", "bluebed", "redbed", "yellowbed", "whitebed", "corner1", "corner2");
                case MINIGAME_OITC -> Arrays.asList("center");
                case MINIGAME_SKYWARS -> Arrays.asList("spawn1", "spawn2", "spawn3", "spawn4", "center", "corner1", "corner2");
                default -> Arrays.asList("spawn1", "spawn2", "center");
            };
        }



        public boolean isGenerateLoot() {
            switch(this) {
                case DUEL_SKYWARS:
                case MINIGAME_SKYWARS:
                    return true;
                default:
                    return false;
            }
        }

        public boolean canModifyArena() {
            switch(this) {
                case DUEL_SKYWARS:
                case MINIGAME_SKYWARS:
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
                case DUEL_FIREBALL_FIGHT:
                case MINIGAME_FIREBALL_BLITZ:
                case MINIGAME_SKYWARS:
                case SPLEEF:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isRandomSpawnLocation() {
            switch(this) {
                case MINIGAME_OITC:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isBedRespawn() {
            return switch (this) {
                case DUEL_BED_FIGHT, DUEL_FIREBALL_FIGHT, MINIGAME_FIREBALL_BLITZ -> true;
                default -> false;
            };
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
                case DUEL_FIREBALL_FIGHT: return Material.FIREBALL;
                case SPLEEF: return Material.SNOW_BLOCK;
                case HCF_TEAMFIGHT: return Material.DIAMOND_SWORD;
                case FFA: return Material.DIAMOND;
                case EVENT_SUMO: return Material.SLIME_BALL;
                case MINIGAME_FIREBALL_BLITZ: return Material.FLINT_AND_STEEL;
                case MINIGAME_SKYWARS: return Material.ENDER_PEARL;
                case MINIGAME_OITC: return Material.ARROW;
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
