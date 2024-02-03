package camp.pvp.practice.utils;

import org.bukkit.ChatColor;

public class Colors {

    public static String[] get(String... s) {
        String[] strings = new String[s.length];
        for (int i = 0; i < s.length; i++) {
            strings[i] = get(s[i]);
        }
        return strings;
    }

    public static String get(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String strip(String s) {
        return ChatColor.stripColor(get(s));
    }
}
