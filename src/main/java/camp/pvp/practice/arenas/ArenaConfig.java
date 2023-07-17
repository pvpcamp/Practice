package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

public class ArenaConfig {

    private Practice plugin;
    private Logger logger;
    private ArenaManager manager;
    private File file;
    private @Getter YamlConfiguration config;
    public ArenaConfig(Practice plugin, ArenaManager manager) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.manager = manager;

        this.file = new File(plugin.getDataFolder(), "arenas.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("arenas.yml", false);
        }

        this.config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection arenaSection = config.getConfigurationSection("arenas");
        if(arenaSection != null) {
            Set<String> arenaKeys = arenaSection.getKeys(false);

            for (String s : arenaKeys) {
                if(config.isSet("arenas." + s)) {
                    load(s);
                }
            }
        } else {
            logger.warning("No arenas have been found in arenas.yml!");
        }
    }

    public void load(String name) {
        String path = "arenas." + name + ".";
        Arena arena = new Arena(name);

        arena.setType(Arena.Type.valueOf(config.getString(path + "type")));
        arena.setDisplayName(config.getString(path + "display_name"));
        arena.setEnabled(config.getBoolean(path + "enabled"));

        for(String s : config.getConfigurationSection(path + "positions").getKeys(false)) {
            String p = path + "positions." + s;

            ArenaPosition pos = new ArenaPosition(s, (Location) config.get(p, Location.class));

            arena.getPositions().put(pos.getPosition(), pos);
        }

        manager.getArenas().add(arena);

        this.logger.info("Loaded arena '" + arena.getName() + "'.");
    }

    public void export(Arena arena) {
        String path = "arenas." + arena.getName();

        config.set(path, " ");

        config.set(path + ".type", arena.getType().toString());
        config.set(path + ".display_name", arena.getDisplayName());
        config.set(path + ".enabled", arena.isEnabled());

        for(ArenaPosition position : arena.getPositions().values()) {
            Location location = position.getLocation();
            String p = path + ".positions." + position.getPosition();
            config.set(p, location);
        }

        this.logger.info("Exported arena '" + arena.getName() + "'.");
    }

    public void shutdown() {
        config = new YamlConfiguration();
        for(Arena arena : manager.getArenas()) {
            export(arena);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
