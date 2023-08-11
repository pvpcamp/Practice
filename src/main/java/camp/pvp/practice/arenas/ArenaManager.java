package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;
import java.util.logging.Logger;

public class ArenaManager {

    private Practice plugin;
    private Logger logger;
    private ArenaConfig arenaConfig;
    private @Getter Set<Arena> arenas;
    private @Getter ArenaResetter arenaResetter;
    private @Getter ArenaCopyQueue arenaCopyQueue;
    private @Getter @Setter ArenaBlockUpdater arenaBlockUpdater;
    private @Getter @Setter ArenaDeleter arenaDeleter;
    public ArenaManager(Practice plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.arenas = new HashSet<>();

        this.logger.info("Starting ArenaManager...");

        this.arenaConfig = new ArenaConfig(plugin, this);
        this.arenaResetter = new ArenaResetter(this);
        Bukkit.getScheduler().runTaskTimer(plugin, arenaResetter, 0, 10);

        this.arenaCopyQueue = new ArenaCopyQueue(plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, arenaCopyQueue, 0, 4);

        this.scanBlocks();
    }

    public Arena getArenaFromName(String name) {
        for(Arena arena : arenas) {
            if(arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }

        return null;
    }

    public Arena selectRandomArena(DuelKit kit) {
        List<Arena> arenas = new ArrayList<>();
        if(kit.isBuild()) {
            for(Arena a : getOriginalArenas()) {
                if(a.isEnabled()) {
                    if (kit.getArenaTypes().contains(a.getType())) {
                        for (Arena copy : getArenaCopies(a)) {
                            if (copy.isEnabled() && !copy.isInUse()) {
                                arenas.add(copy);
                            }
                        }
                    }
                }
            }
        } else {
            for(Arena a : getArenas()) {
                if(a.isEnabled()) {
                    if(kit.getArenaTypes().contains(a.getType())) {
                        arenas.add(a);
                    }
                }
            }
        }

        Collections.shuffle(arenas);
        if(arenas.isEmpty()) {
            return null;
        } else {
            return arenas.get(0);
        }
    }

    public List<Arena> getOriginalArenas() {
        List<Arena> arenas = new ArrayList<>();
        for(Arena arena : getArenas()) {
            if(!arena.isCopy()) {
                arenas.add(arena);
            }
        }
        return arenas;
    }

    public void scanBlocks() {
        this.logger.info("Scanning arenas for important blocks.");

        int arenas = 0;
        for(Arena arena : getArenas()) {
            if(arena.hasValidPositions()) {
                ArenaPosition corner1 = arena.getPositions().get("corner1");
                ArenaPosition corner2 = arena.getPositions().get("corner2");

                if(corner1 != null && corner2 != null) {

                    arena.getBeds().clear();
                    arena.getChests().clear();
                    arena.getBlocks().clear();

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
                                            arena.getBeds().add(block.getLocation());
                                            break;
                                        case CHEST:
                                        case TRAPPED_CHEST:
                                            arena.getChests().add(block.getLocation());
                                        default:
                                            arena.getBlocks().add(location);
                                    }
                                }
                            }
                        }
                    }

                    arenas++;
                }
            }
        }

        this.logger.info("Arena scanner completed, " + arenas + " arenas have been scanned for blocks.");
    }

    public void deleteArena(Arena arena) {
        this.arenas.remove(arena);

        if(arena.isCopy()) {

        }
    }

    public int getNextCopyNumber(Arena arena) {
        int hn = 0;
        for(Arena a : getArenaCopies(arena)) {
            String cn = a.getName().replace(arena.getName() + "_copy_", "");
            int number = 0;
            try {
                number = Integer.parseInt(cn);
            } catch(NumberFormatException e) {
                e.printStackTrace();
            }

            if(hn < number) {
                hn = number;
            }
        }

        return hn + 1;
    }

    public Arena createCopy(Arena arena, int xD, int zD) {
        int hn = getNextCopyNumber(arena);

        Arena copy = new Arena(arena.getName() + "_copy_" + hn);
        copy.setDisplayName(arena.getDisplayName());
        copy.setEnabled(arena.isEnabled());
        copy.setXDifference(xD);
        copy.setZDifference(zD);
        copy.copyPositions(arena);
        copy.setParent(arena.getName());
        copy.setType(arena.getType());

        return copy;
    }

    public Arena createCopy(Arena arena, int xD, int zD, int override) {
        Arena copy = new Arena(arena.getName() + "_copy_" + override);
        copy.setDisplayName(arena.getDisplayName());
        copy.setEnabled(arena.isEnabled());
        copy.setXDifference(xD);
        copy.setZDifference(zD);
        copy.copyPositions(arena);
        copy.setParent(arena.getName());
        copy.setType(arena.getType());

        return copy;
    }

    public Set<Arena> getArenaCopies(Arena arena) {
        Set<Arena> arenas = new HashSet<>();
        for(Arena a : getArenas()) {
            if(a.getParent() != null && a.getParent().equals(arena.getName())) {
                arenas.add(a);
            }
        }

        return arenas;
    }

    public void updateArenaCopies(Arena arena) {
        for(Arena a : getArenaCopies(arena)) {
            a.copyPositions(arena);
        }
    }

    public void shutdown() {
        arenaConfig.shutdown();
    }
}
