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
    private @Getter ArenaCopyQueue arenaCopyQueue;
    private @Getter @Setter ArenaBlockUpdater arenaBlockUpdater;
    private @Getter @Setter ArenaDeleter arenaDeleter;
    public ArenaManager(Practice plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.arenas = new HashSet<>();

        logger.info("Starting ArenaManager...");

        arenaConfig = new ArenaConfig(plugin, this);

        arenaCopyQueue = new ArenaCopyQueue(plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, arenaCopyQueue, 0, 4);

        scanBlocks();
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
        String m = "Scanning arenas for all blocks.";

        this.logger.info(m);

        int arenas = 0;
        for(Arena arena : getArenas()) {
            if(arena.hasValidPositions()) {
                arena.scanArena();

                arenas++;
            }
        }

        m = "Arena scanner has finished scanning " + arenas + " arenas.";
        this.logger.info(m);
    }

    public void deleteArena(Arena arena) {
        this.arenas.remove(arena);

        if(arena.isCopy()) {
            setArenaDeleter(new ArenaDeleter(this, arena));
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
