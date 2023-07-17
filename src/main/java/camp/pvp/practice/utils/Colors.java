package camp.pvp.practice.utils;

import org.bukkit.ChatColor;

public class Colors {
    public static String get(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String strip(String s) {
        return ChatColor.stripColor(get(s));
    }
}
