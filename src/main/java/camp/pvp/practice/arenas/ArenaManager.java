package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Logger;

public class ArenaManager {

    private Practice plugin;
    private Logger logger;
    private ArenaConfig arenaConfig;
    private @Getter Set<Arena> arenas;
    private @Getter ArenaBlockRestorer blockRestorer;
    private BukkitTask arenaBlockRestorerTask;
    public ArenaManager(Practice plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.arenas = new HashSet<>();

        logger.info("Initialized ArenaManager.");

        arenaConfig = new ArenaConfig(plugin, this);

        blockRestorer = new ArenaBlockRestorer();
        arenaBlockRestorerTask = Bukkit.getScheduler().runTaskTimer(plugin, blockRestorer, 0, 1);

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

    public Arena selectRandomArena(DuelKit kit) {
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

        Collections.shuffle(arenas);
        if(arenas.isEmpty()) {
            return null;
        } else {
            return arenas.get(0);
        }
    }

    public Arena selectRandomArena(Arena.Type type) {
        List<Arena> arenas = new ArrayList<>(getArenaForType(type));
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
        for(Arena arena : getArenas()) {
            if(arena.isEnabled() && !arena.isInUse() && !arena.isCopy() && arena.getType().equals(type)) {
                arenas.add(arena);
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
        String m = "Scanning arenas for all blocks.";

        this.logger.info(m);

        int arenas = 0;
        for(Arena arena : getArenas()) {
            if(arena.hasValidPositions()) {
                arena.scanArena();

                arenas++;
            }
        }

        logger.info("Arena scanner has finished scanning " + arenas + " arenas.");
    }

    public void updateAndResetCopies() {

        for(Arena arena : getArenas()) {
            arena.resetArena();
        }
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
        copy.setParent(arena.getName());
        copy.setType(arena.getType());
        copy.updateCopy(true);

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

    public void updateArenaCopies(Arena arena, boolean reset) {
        for(Arena a : getArenaCopies(arena)) {
            a.updateCopy(reset);
        }
    }

    public void shutdown() {
        arenaConfig.shutdown();
    }
}
