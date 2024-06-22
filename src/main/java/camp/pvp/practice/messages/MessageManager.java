package camp.pvp.practice.messages;

import camp.pvp.practice.Practice;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class MessageManager {

    private final Practice plugin;
    private final Logger logger;
    private File file;
    private @Getter YamlConfiguration config;
    private @Getter String primaryColor, secondaryColor, defaultColor;
    public MessageManager(Practice plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        this.file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource("messages.yml", false);
        }

        reload();
    }

    public String getMessage(String path) {
        Object obj = config.get("messages." + path);

        if(obj == null) {
            logger.warning("Message path " + path + " does not exist in messages.yml!");
            return null;
        }

        String message = null;

        if(obj instanceof String) {
            message = config.getString("messages." + path);
        }

        if(obj instanceof List<?>) {
            List<String> list = config.getStringList("messages." + path);
            StringBuilder builder = new StringBuilder();
            int x = 0;
            for(String s : list) {
                builder.append(s);
                x++;
                if(x < list.size()) {
                    builder.append("\n");
                }
            }
            message = builder.toString();
        }

        return translate(message);
    }

    public String translate(String message) {
        message = message
                .replace("%p%", primaryColor)
                .replace("%s%", secondaryColor)
                .replace("%d%", defaultColor);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void sendMessage(String path, Player... players) {
        for (Player player : players) {
            player.sendMessage(getMessage(path));
        }
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);

        this.primaryColor = config.getString("defaults.colors.primary");
        this.secondaryColor = config.getString("defaults.colors.secondary");
        this.defaultColor = config.getString("defaults.colors.default");
    }
}
