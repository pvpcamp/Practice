package camp.pvp.kits;

import camp.pvp.games.GameInventory;
import camp.pvp.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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

    public boolean isEditable() {
        switch(this) {
            case NO_DEBUFF:
            case DEBUFF:
            case SOUP:
                return true;
            default:
                return false;
        }
    }

    public GameInventory getGameInventory() {
        GameInventory inv = new GameInventory();
        ItemStack[] armor = inv.getArmor(), inventory = inv.getInventory();
        switch(this) {
            case NO_DEBUFF:
                armor[0] = new ItemStack(Material.DIAMOND_HELMET);
                armor[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                armor[0].addEnchantment(Enchantment.DURABILITY, 2);

                armor[1] = new ItemStack(Material.DIAMOND_CHESTPLATE);
                armor[2] = new ItemStack(Material.DIAMOND_LEGGINGS);
                armor[3] = new ItemStack(Material.DIAMOND_BOOTS);
                break;
            default:
                break;

        }

        return inv;
    }

    public void apply(Player player) {
        PlayerInventory pi = player.getInventory();
        GameInventory gi = this.getGameInventory();

        PlayerUtils.reset(player);

        pi.setArmorContents(gi.getArmor());
        pi.setContents(gi.getInventory());
    }
}
