package camp.pvp.arenas;

import camp.pvp.Practice;
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

    public void shutdown() {
        arenaConfig.shutdown();
    }
}
