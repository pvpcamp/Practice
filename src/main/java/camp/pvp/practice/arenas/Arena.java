package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.utils.BukkitReflection;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.internal.LocalWorldAdapter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.scheduler.BukkitTask;
import xyz.refinedev.spigot.api.chunk.ChunkAPI;
import xyz.refinedev.spigot.api.chunk.ChunkSnapshot;

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
        setWorldId(parent.getWorldId());

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

        int lowChunkX = minX >> 4;
        int lowChunkZ = minZ >> 4;
        int highChunkX = maxX >> 4;
        int highChunkZ = maxZ >> 4;

        lowChunkX--;
        lowChunkZ--;
        highChunkX++;
        highChunkZ++;

        for (int x = lowChunkX; x < highChunkX; x++) {
            for (int z = lowChunkZ; z < highChunkZ; z++) {
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

        int minX, minY, minZ, maxX, maxY, maxZ;
        Location c1 = getPositions().get("corner1").getLocation(), c2 = getPositions().get("corner2").getLocation();
        minX = Math.min(c1.getBlockX(), c2.getBlockX());
        minY = Math.min(c1.getBlockY(), c2.getBlockY());
        minZ = Math.min(c1.getBlockZ(), c2.getBlockZ());
        maxX = Math.max(c1.getBlockX(), c2.getBlockX());
        maxY = Math.max(c1.getBlockY(), c2.getBlockY());
        maxZ = Math.max(c1.getBlockZ(), c2.getBlockZ());

        logger.info("[Arena#copyBlocks] Copying blocks with WorldEdit for " + getName() + ".");

        com.sk89q.worldedit.Vector min = new com.sk89q.worldedit.Vector(minX - xDifference, minY, minZ - zDifference);
        com.sk89q.worldedit.Vector max = new com.sk89q.worldedit.Vector(maxX - xDifference, maxY, maxZ - xDifference);
        com.sk89q.worldedit.Vector origin = new com.sk89q.worldedit.Vector(minX, minY, minZ);

        CuboidRegion region = new CuboidRegion(min, max);
        LocalWorld world = BukkitUtil.getLocalWorld(c1.getWorld());
        region.setWorld(world);

        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(world, region, min, editSession, origin);
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }

        logger.info("[Arena#copyBlocks] Copying solid block locations for " + getName() + ".");

        List<Location> newSolidBlocks = new ArrayList<>();

        for(Location location : getParent().getSolidBlocks()) {
            Location newLocation = location.clone().add(xDifference, 0, zDifference);
            newSolidBlocks.add(newLocation);
        }

        setSolidBlocks(newSolidBlocks);

        logger.info("[Arena#copyBlocks] Refreshing chunk information and snapshots for " + getName() + ".");

        generateStoredChunks();
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
        DUEL, DUEL_FLAT, DUEL_BUILD, DUEL_SUMO, DUEL_HCF, DUEL_SKYWARS, DUEL_BED_FIGHT, DUEL_FIREBALL_FIGHT, DUEL_BRIDGE, SPLEEF, FFA, EVENT_SUMO,
        MINIGAME_OITC, MINIGAME_TNT_TAG;

        public List<String> getValidPositions() {
            return switch (this) {
                case DUEL_BED_FIGHT, DUEL_FIREBALL_FIGHT -> Arrays.asList("spawn1", "spawn2", "corner1", "corner2", "bluebed", "redbed");
                case DUEL_BUILD, DUEL_SKYWARS, SPLEEF -> Arrays.asList("spawn1", "spawn2", "center", "corner1", "corner2");
                case EVENT_SUMO -> Arrays.asList("spawn1", "spawn2", "lobby");
                case FFA, MINIGAME_TNT_TAG -> Arrays.asList("spawn");
                case MINIGAME_OITC -> Arrays.asList("center");
                default -> Arrays.asList("spawn1", "spawn2", "center");
            };
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
                case DUEL_FIREBALL_FIGHT:
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
                case DUEL_BED_FIGHT, DUEL_FIREBALL_FIGHT -> true;
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
                case FFA: return Material.DIAMOND;
                case EVENT_SUMO: return Material.SLIME_BALL;
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
