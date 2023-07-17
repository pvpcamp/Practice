package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
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

        this.logger.info("Starting ArenaManager...");

        this.arenaConfig = new ArenaConfig(plugin, this);
    }

    public Arena getArenaFromName(String name) {
        for(Arena arena : arenas) {
            if(arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }

        return null;
    }

    public void deleteArena(Arena arena) {
        this.arenas.remove(arena);
    }

    public void shutdown() {
        arenaConfig.shutdown();
    }
}
