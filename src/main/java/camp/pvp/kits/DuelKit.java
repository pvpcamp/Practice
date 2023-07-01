package camp.pvp.kits;

import org.bukkit.ChatColor;

public enum DuelKit {
    NO_DEBUFF, DEBUFF, BOXING, SOUP;

    public String getDisplayName() {
        switch(this) {
            case NO_DEBUFF:
                return "No Debuff";
            case DEBUFF:
                return "Debuff";
            case BOXING:
                return "Boxing";
            case SOUP:
                return "Soup";
            default:
                return null;
        }
    }

    public ChatColor getColor() {
        switch(this) {
            case NO_DEBUFF:
                return ChatColor.RED;
            case DEBUFF:
                return ChatColor.DARK_GREEN;
            case BOXING:
                return ChatColor.GOLD;
            case SOUP:
                return ChatColor.GREEN;
            default:
                return null;
        }
    }
}
