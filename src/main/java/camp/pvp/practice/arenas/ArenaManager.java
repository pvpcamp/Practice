package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.logging.Logger;

public class ArenaManager {

    private Practice plugin;
    private Logger logger;
    private ArenaConfig arenaConfig;
    private @Getter Set<Arena> arenas;
    private @Getter ArenaResetter arenaResetter;
    public ArenaManager(Practice plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.arenas = new HashSet<>();

        this.logger.info("Starting ArenaManager...");

        this.arenaConfig = new ArenaConfig(plugin, this);
        this.arenaResetter = new ArenaResetter(this);
        Bukkit.getScheduler().runTaskTimer(plugin, arenaResetter, 0, 4);
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

    public void deleteArena(Arena arena) {
        this.arenas.remove(arena);
    }

    public Arena createCopy(Arena arena, int xD, int zD) {
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

        Arena copy = new Arena(arena.getName() + "_copy_" + (hn + 1));
        copy.setDisplayName(arena.getDisplayName());
        copy.setEnabled(arena.isEnabled());
        copy.copyPositions(arena, xD, zD);
        copy.setParent(arena.getName());
        copy.setType(arena.getType());
        copy.setXDifference(xD);
        copy.setZDifference(zD);

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

    public void shutdown() {
        arenaConfig.shutdown();
    }
}
