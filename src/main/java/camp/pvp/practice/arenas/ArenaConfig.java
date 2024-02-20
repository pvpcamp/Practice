package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

        logger.info("Loading arenas from arenas.yml.");

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
        arena.setXDifference(config.getInt(path + "x_difference"));
        arena.setZDifference(config.getInt(path + "z_difference"));
        arena.setVoidLevel(config.getInt(path + "void_level", 0));
        arena.setBuildLimit(config.getInt(path + "build_limit", 256));

        if(config.isSet(path + "parent")) {
            arena.setParentName(config.getString(path + "parent"));
        }

        for(String s : config.getConfigurationSection(path + "positions").getKeys(false)) {
            String p = path + "positions." + s;

            ArenaPosition pos = new ArenaPosition(s, (Location) config.get(p, Location.class));

            arena.getPositions().put(pos.getPosition(), pos);
        }

        List<String> serializedChests = config.getStringList(path + "loot_chests");
        for(String serializedChest : serializedChests) {
            arena.getLootChests().add(LootChest.deserialize(serializedChest));
        }

        if(!arena.hasValidPositions()) {
            arena.setEnabled(false);
            this.logger.info("Arena '" + arena.getName() + "' does not have all of its valid positions, this must be fixed.");
        }

        manager.getArenas().add(arena);
    }

    public void export(Arena arena) {
        String path = "arenas." + arena.getName();

        config.set(path, " ");
        config.set(path + ".type", arena.getType().name());
        config.set(path + ".display_name", arena.getDisplayName());
        config.set(path + ".enabled", arena.isEnabled());
        config.set(path + ".x_difference", arena.getXDifference());
        config.set(path + ".z_difference", arena.getZDifference());
        config.set(path + ".void_level", arena.getVoidLevel());
        config.set(path + ".build_limit", arena.getBuildLimit());

        if(arena.getParentName() != null) {
            config.set(path + ".parent", arena.getParentName());
        }

        for(ArenaPosition position : arena.getPositions().values()) {
            Location location = position.getLocation();
            String p = path + ".positions." + position.getPosition();
            config.set(p, location);
        }

        List<String> serializedChests = new ArrayList<>();
        for(LootChest chest : arena.getLootChests()) {
            serializedChests.add(chest.serialize());
        }

        config.set(path + ".loot_chests", serializedChests);
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
