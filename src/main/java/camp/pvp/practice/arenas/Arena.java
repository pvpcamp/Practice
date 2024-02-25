package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
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
    private List<Location> randomSpawnLocations;
    private List<LootChest> lootChests;

    private boolean enabled, inUse;
    private String parentName;
    private int xDifference, zDifference, buildLimit, voidLevel;

    private @Getter List<Location> beds, allBlocks, blocks, chests;
    private @Getter Queue<StoredBlock> blockQueue;

    public Arena(String name) {
        this.name = name;
        this.displayName = name;
        this.type = Type.DUEL;
        this.positions = new HashMap<>();
        this.randomSpawnLocations = new ArrayList<>();
        this.lootChests = new ArrayList<>();

        this.beds = new ArrayList<>();
        this.allBlocks = new ArrayList<>();
        this.blocks = new ArrayList<>();
        this.chests = new ArrayList<>();
        this.blockQueue = new LinkedList<>();

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
    public void updateCopy(boolean reset) {

        if(!isCopy()) return;

        Arena parent = Practice.getInstance().getArenaManager().getArenaFromName(getParentName());

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
            for(LootChest lootChest : lootChests) {
                Location l = lootChest.getLocation();
                l.getBlock().setType(Material.CHEST);
                l.getBlock().getState().update(true);

                lootChest.generateLoot(lootChests);
            }
        }
    }

    public void scanArena() {

        ArenaPosition corner1 = getPositions().get("corner1");
        ArenaPosition corner2 = getPositions().get("corner2");

        if(corner1 != null && corner2 != null) {

            getBeds().clear();
            getChests().clear();
            getAllBlocks().clear();

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

                        getAllBlocks().add(location);
                    }
                }
            }
        }
    }

    public void clearBlocks() {
        scanArena();
        for(Location location : getAllBlocks()) {
            location.getBlock().setType(Material.AIR);
        }
    }

    /***
     * Resets the arena.
     */
    public void resetArena() {
        if (!getType().isResetAfterGame()) return;
        if (!isCopy()) return;

        for(Location parentLocation : Practice.getInstance().getArenaManager().getArenaFromName(getParentName()).getAllBlocks()) {
            Location location = parentLocation.clone().add(xDifference, 0, zDifference);

            Block parentBlock = parentLocation.getBlock();
            Block block = location.getBlock();

            if(parentBlock.getType() != block.getType()) {
                StoredBlock storedBlock = new StoredBlock(parentBlock, location);
                blockQueue.add(storedBlock);
            }
        }

        Practice.getInstance().sendDebugMessage("Resetting arena " + getName() + " with " + blockQueue.size() + " blocks.");

        Practice.getInstance().getArenaManager().getBlockRestorer().addArena(this);
    }

    public boolean isOriginalBlock(Location location) {

        if (!type.equals(Type.DUEL_BED_FIGHT)) return blocks.contains(location);

        for (ArenaPosition position : positions.values()) {
            if (position.getPosition().equalsIgnoreCase("bluebed") || position.getPosition().equalsIgnoreCase("redbed")) {
                Location l = position.getLocation();

                for (int x = l.getBlockX() - 4; x < l.getBlockX() + 4; x++) {
                    for (int y = l.getBlockY(); y < l.getBlockY() + 3; y++) {
                        for (int z = l.getBlockZ() - 4; z < l.getBlockZ() + 4; z++) {
                            Location blockLocation = new Location(l.getWorld(), x, y, z);
                            Block block = blockLocation.getBlock();
                            if (block.equals(location.getBlock())) {
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
        DUEL, DUEL_FLAT, DUEL_BUILD, DUEL_SUMO, DUEL_HCF, DUEL_SKYWARS, DUEL_BED_FIGHT, DUEL_BRIDGE, SPLEEF, HCF_TEAMFIGHT, FFA, EVENT_SUMO,
        MINIGAME_SKYWARS, MINIGAME_OITC;

        public List<String> getValidPositions() {
            return switch (this) {
                case DUEL_BED_FIGHT -> Arrays.asList("spawn1", "spawn2", "corner1", "corner2", "bluebed", "redbed");
                case DUEL_BUILD, DUEL_SKYWARS, SPLEEF -> Arrays.asList("spawn1", "spawn2", "center", "corner1", "corner2");
                case EVENT_SUMO -> Arrays.asList("spawn1", "spawn2", "lobby");
                case FFA -> Arrays.asList("spawn");
                case MINIGAME_OITC -> Arrays.asList("center");
                case MINIGAME_SKYWARS -> Arrays.asList("spawn1", "spawn2", "spawn3", "spawn4", "center");
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

        public boolean isRandomSpawnLocation() {
            switch(this) {
                case MINIGAME_OITC:
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
