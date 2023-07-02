package camp.pvp.arenas;

import camp.pvp.Practice;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
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

            logger.info("Found " + arenaKeys.size() + " arenas in config file.");

            for (String s : arenaKeys) {
                load(s);
            }
        } else {
            logger.warning("No arenas have been found in arenas.yml!");
        }
    }

    public void load(String name) {
        String path = "arenas." + name + ".";
        Arena arena = new Arena();

        arena.setName(name);
        arena.setType(Arena.Type.valueOf(config.getString(path + "type")));
        arena.setDisplayName(config.getString(path + "display_name"));
        arena.setEnabled(config.getBoolean(path + "enabled"));

        for(String s : config.getConfigurationSection(path + "positions").getKeys(false)) {
            String p = path + "positions." + s + ".";

            ArenaPosition pos = new ArenaPosition(p, new Location(
                    Bukkit.getWorld(config.getString(p + "world")),
                    config.getDouble(p + "x"),
                    config.getDouble(p + "y"),
                    config.getDouble(p + "z"),
                    (float) config.getDouble(p + "yaw"),
                    (float) config.getDouble(p + "pitch")
            ));

            arena.getPositions().add(pos);
        }

        this.logger.info("Loaded arena '" + arena.getName() + "'.");
    }

    public void export(Arena arena) {
        String path = "arenas." + arena.getName();

        config.set(path, " ");

        config.set(path + ".type", arena.getType().toString());
        config.set(path + ".display_name", arena.getDisplayName());
        config.set(path + ".enabled", arena.isEnabled());

        for(ArenaPosition position : arena.getPositions()) {
            Location location = position.getLocation();
            String p = path + ".positions." + position.getPosition() + ".";
            config.set(p + "world", location.getWorld().getName());
            config.set(p + "x", (double) location.getX());
            config.set(p + "y", (double) location.getY());
            config.set(p + "z", (double) location.getZ());
            config.set(p + "yaw", (float) location.getYaw());
            config.set(p + "pitch", (float) location.getPitch());
        }

        this.logger.info("Exported arena '" + arena.getName() + "'.");
    }

    public void shutdown() {
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
