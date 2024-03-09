package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.GameKit;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Logger;

public class ArenaManager {

    private Practice plugin;
    private Logger logger;
    private ArenaConfig arenaConfig;
    private @Getter Set<Arena> arenas;
    public ArenaManager(Practice plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.arenas = new HashSet<>();

        logger.info("Initialized ArenaManager.");

        arenaConfig = new ArenaConfig(plugin, this);

        scanBlocks();

        logger.info("ArenaManager has finished the startup process.");
    }

    public Arena getArenaFromName(String name) {
        for(Arena arena : arenas) {
            if(arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }

        return null;
    }

    public Arena selectRandomArena(GameKit kit) {
        List<Arena> arenas = new ArrayList<>();
        if(kit.isBuild()) {
            for(Arena a : getOriginalArenas()) {

                if(!a.isEnabled()) continue;

                if(!kit.getArenaTypes().contains(a.getType())) continue;

                for (Arena copy : getArenaCopies(a)) {
                    if (copy.isEnabled() && !copy.isInUse()) {
                        arenas.add(copy);
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

        arenas.forEach(arena -> {
            if(!arena.hasValidPositions()) {
                arenas.remove(arena);
            }
        });

        Collections.shuffle(arenas);

        return arenas.isEmpty() ? null : arenas.get(0);
    }

    public Arena selectRandomArena(Arena.Type type) {
        List<Arena> arenas = getArenaForType(type);

        arenas.forEach(arena -> {
            if(!arena.hasValidPositions()) {
                arenas.remove(arena);
            }
        });

        Collections.shuffle(arenas);

        return arenas.isEmpty() ? null : arenas.get(0);
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

    public List<Arena> getArenaForType(Arena.Type type) {
        List<Arena> arenas = new ArrayList<>();

        if(type.isBuild()) {
            for(Arena a : getOriginalArenas()) {

                if(!a.isEnabled()) continue;

                if(!a.getType().equals(type)) continue;

                for (Arena copy : getArenaCopies(a)) {
                    if (copy.isEnabled() && !copy.isInUse()) {
                        arenas.add(copy);
                    }
                }
            }
        } else {
            for (Arena arena : getArenas()) {
                if (arena.isEnabled() && !arena.isInUse() && !arena.isCopy() && arena.getType().equals(type)) {
                    arenas.add(arena);
                }
            }
        }

        return arenas;
    }

    public List<Arena> getArenaForTypeAny(Arena.Type type) {
        List<Arena> arenas = new ArrayList<>();
        for(Arena arena : getArenas()) {
            if(!arena.isCopy() && arena.getType().equals(type)) {
                arenas.add(arena);
            }
        }

        return arenas;
    }

    public void scanBlocks() {
        this.logger.info("Scanning arenas for all blocks.");

        int arenas = 0;
        for(Arena arena : getArenas()) {
            if(!arena.isCopy() && arena.hasValidPositions()) {
                arena.scanArena();

                logger.info("Scanned arena " + arena.getName() + ".");

                arenas++;
            }
        }

        logger.info("Arena scanner has finished scanning " + arenas + " arenas.");

        copyArenaBlockLocations();

        logger.info("Arena block locations have been copied. If the arenas do not have the correct blocks, please update the parent arena accordingly.");
    }

    public void copyArenaBlockLocations() {
        for(Arena arena : getArenas()) {
            if(arena.isCopy()) {
                Arena parent = getArenaFromName(arena.getParentName());
                if(parent != null) {
                    List<Location> solidBlocks = parent.getSolidBlocks();
                    List<Location> newSolidBlocks = new ArrayList<>();

                    for(Location location : solidBlocks) {
                        newSolidBlocks.add(location.clone().add(arena.getXDifference(), 0, arena.getZDifference()));
                    }

                    arena.setSolidBlocks(newSolidBlocks);
                }

                arena.refreshChunkSnapshots();
            }
        }
    }

    public Location getNextAvailableArenaLocation(World world) {

        List<Location> locations = new ArrayList<>();
        for(Arena arena : getArenas()) {
            for(ArenaPosition position : arena.getPositions().values()) {
                Location l = position.getLocation();
                if(l.getWorld().equals(world)) {
                    locations.add(l);
                }
            }
        }

        for(int x = 2000; x < 1000000; x += 1000) {

            boolean found = false;
            for (Location l  : locations) {
                int difference = l.getBlockX() - x;
                if (difference > -200 && difference < 200) {
                    found = true;
                    break;
                }
            }

            if(!found) {
                return new Location(world, x, 80, 0);
            }
        }

        return null;
    }

    public void deleteArena(Arena arena) {
        this.arenas.remove(arena);

        if(arena.isCopy()) {
            arena.clearBlocks();
        } else {
            for(Arena a : getArenaCopies(arena)) {
                deleteArena(a);
            }
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

    public Arena createCopy(Arena arena, int fromX, int fromZ, int xD, int zD) {
        int hn = getNextCopyNumber(arena);

        Arena copy = new Arena(arena.getName() + "_copy_" + hn);
        copy.setDisplayName(arena.getDisplayName());
        copy.setEnabled(arena.isEnabled());
        copy.setXDifference(fromX + xD);
        copy.setZDifference(fromZ + zD);
        copy.setParentName(arena.getName());
        copy.setType(arena.getType());
        copy.updateCopy(arena, true);

        return copy;
    }

    public Set<Arena> getArenaCopies(Arena arena) {
        Set<Arena> arenas = new HashSet<>();
        for(Arena a : getArenas()) {
            if(a.getParentName() != null && a.getParentName().equals(arena.getName())) {
                arenas.add(a);
            }
        }

        return arenas;
    }

    public void updateArenaCopies(Arena arena, boolean reset) {
        for(Arena a : getArenaCopies(arena)) {
            a.updateCopy(arena, reset);
        }
    }

    public void shutdown() {
        arenaConfig.shutdown();
    }
}
