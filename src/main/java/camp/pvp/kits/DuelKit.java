package camp.pvp.kits;

import camp.pvp.arenas.Arena;
import camp.pvp.games.GameInventory;
import camp.pvp.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public enum DuelKit {
    NO_DEBUFF, DEBUFF, CLASSIC, HCF, BOXING, SOUP, BUILD_UHC;

    public String getDisplayName() {
        switch(this) {
            case NO_DEBUFF:
                return "No Debuff";
            case HCF:
                return "HCF";
            case CLASSIC:
                return "Classic";
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
            case HCF:
                return ChatColor.DARK_RED;
            case CLASSIC:
                return ChatColor.LIGHT_PURPLE;
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
            case HCF:
            case SOUP:
                return true;
            default:
                return false;
        }
    }

    public Arena.Type getArenaType() {
        switch(this) {
            case HCF:
                return Arena.Type.DUEL_HCF;
            default:
                return Arena.Type.DUEL;
        }
    }

    public boolean isBuild() {
        switch(this) {
            case BUILD_UHC:
                return true;
            default:
                return false;
        }
    }

    public boolean isQueueable () {
        switch(this) {
            case NO_DEBUFF:
            case HCF:
            case CLASSIC:
                return true;
            default:
                return false;
        }
    }

    public boolean isHunger() {
        switch(this) {
            case SOUP:
            case BOXING:
                return false;
            default:
                return true;
        }
    }

    public boolean isRanked() {
        return false;
    }

    public ItemStack getIcon() {
        ItemStack item = null;
        switch(this) {
            case NO_DEBUFF:
                Potion potion = new Potion(PotionType.INSTANT_HEAL);
                potion.setSplash(true);
                item = potion.toItemStack(1);
                break;
            case HCF:
                item = new ItemStack(Material.FENCE);
                break;
            case CLASSIC:
                item = new ItemStack(Material.DIAMOND_SWORD);
                break;
        }

        return item;
    }

    public GameInventory getGameInventory() {
        GameInventory inventory = new GameInventory();
        ItemStack[] armor = inventory.getArmor(), inv = inventory.getInventory();
        switch(this) {
            case NO_DEBUFF:
                armor[3] = new ItemStack(Material.DIAMOND_HELMET);
                armor[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                armor[3].addEnchantment(Enchantment.DURABILITY, 3);

                armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
                armor[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                armor[2].addEnchantment(Enchantment.DURABILITY, 3);

                armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
                armor[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                armor[1].addEnchantment(Enchantment.DURABILITY, 3);

                armor[0] = new ItemStack(Material.DIAMOND_BOOTS);
                armor[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);
                armor[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                armor[0].addEnchantment(Enchantment.DURABILITY, 3);

                inv[0] = new ItemStack(Material.DIAMOND_SWORD);
                inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 3);
                inv[0].addEnchantment(Enchantment.FIRE_ASPECT, 2);
                inv[0].addEnchantment(Enchantment.DURABILITY, 3);

                inv[1] = new ItemStack(Material.ENDER_PEARL, 16);
                inv[2] = new ItemStack(Material.COOKED_BEEF, 64);

                Potion speed = new Potion(PotionType.SPEED, 2);

                Potion fireResistance = new Potion(PotionType.FIRE_RESISTANCE, 1);
                fireResistance.setHasExtendedDuration(true);

                Potion health = new Potion(PotionType.INSTANT_HEAL, 2);
                health.setSplash(true);

                inv[7] = speed.toItemStack(1);
                inv[26] = speed.toItemStack(1);
                inv[35] = speed.toItemStack(1);

                inv[8] = fireResistance.toItemStack(1);

                for(int x = 0; x < 36; x++) {
                    ItemStack i = inv[x];
                    if(i == null) {
                        inv[x] = health.toItemStack(1);
                    }
                }

                break;
            case HCF:
                armor[3] = new ItemStack(Material.DIAMOND_HELMET);
                armor[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                armor[3].addEnchantment(Enchantment.DURABILITY, 3);

                armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
                armor[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                armor[2].addEnchantment(Enchantment.DURABILITY, 3);

                armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
                armor[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                armor[1].addEnchantment(Enchantment.DURABILITY, 3);

                armor[0] = new ItemStack(Material.DIAMOND_BOOTS);
                armor[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);
                armor[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                armor[0].addEnchantment(Enchantment.DURABILITY, 3);

                inv[0] = new ItemStack(Material.DIAMOND_SWORD);
                inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 3);
                inv[0].addEnchantment(Enchantment.FIRE_ASPECT, 2);
                inv[0].addEnchantment(Enchantment.DURABILITY, 3);

                inv[1] = new ItemStack(Material.ENDER_PEARL, 16);
                inv[2] = new ItemStack(Material.GOLDEN_APPLE, 16);
                inv[3] = new ItemStack(Material.FISHING_ROD, 1);

                speed = new Potion(PotionType.SPEED, 2);

                fireResistance = new Potion(PotionType.FIRE_RESISTANCE, 1);
                fireResistance.setHasExtendedDuration(true);

                health = new Potion(PotionType.INSTANT_HEAL, 2);
                health.setSplash(true);

                inv[7] = speed.toItemStack(1);
                inv[17] = speed.toItemStack(1);
                inv[26] = speed.toItemStack(1);
                inv[35] = speed.toItemStack(1);

                inv[8] = fireResistance.toItemStack(1);

                for(int x = 0; x < 36; x++) {
                    ItemStack i = inv[x];
                    if(i == null) {
                        inv[x] = health.toItemStack(1);
                    }
                }

                break;
            case CLASSIC:
                armor[3] = new ItemStack(Material.DIAMOND_HELMET);
                armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
                armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
                armor[0] = new ItemStack(Material.DIAMOND_BOOTS);

                inv[0] = new ItemStack(Material.DIAMOND_SWORD);
                inv[1] = new ItemStack(Material.BOW, 1);
                inv[2] = new ItemStack(Material.FISHING_ROD, 1);
                inv[3] = new ItemStack(Material.GOLDEN_APPLE, 8);

                inv[9] = new ItemStack(Material.ARROW, 12);
                break;
            default:
                break;

        }

        return inventory;
    }

    public void apply(Player player) {
        PlayerInventory pi = player.getInventory();
        GameInventory gi = this.getGameInventory();

        PlayerUtils.reset(player);

        pi.setArmorContents(gi.getArmor());
        pi.setContents(gi.getInventory());
    }
}
