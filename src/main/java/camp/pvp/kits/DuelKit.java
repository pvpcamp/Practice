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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DuelKit {
    NO_DEBUFF, CLASSIC, HCF, BOXING, SUMO;

    public String getDisplayName() {
        switch(this) {
            case NO_DEBUFF:
                return "No Debuff";
            case HCF:
                return "HCF";
            case CLASSIC:
                return "Classic";
            case BOXING:
                return "Boxing";
            case SUMO:
                return "Sumo";
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
            case BOXING:
                return ChatColor.DARK_PURPLE;
            case SUMO:
                return ChatColor.GREEN;
            default:
                return null;
        }
    }

    public boolean isEditable() {
        switch(this) {
            case SUMO:
                return false;
            default:
                return true;
        }
    }

//    public Arena.Type getArenaType() {
//        switch(this) {
//            case HCF:
//                return Arena.Type.DUEL_HCF;
//            default:
//                return Arena.Type.DUEL;
//        }
//    }

    public List<Arena.Type> getArenaTypes() {
        switch(this) {
            case HCF:
                return Arrays.asList(Arena.Type.DUEL_HCF, Arena.Type.HCF_TEAMFIGHT);
            case SUMO:
                return Collections.singletonList(Arena.Type.DUEL_SUMO);
            default:
                return Collections.singletonList(Arena.Type.DUEL);
        }
    }

    public int getUnrankedSlot() {
        switch(this) {
            case NO_DEBUFF:
                return 10;
            case HCF:
                return 11;
            case CLASSIC:
                return 12;
            case BOXING:
                return 15;
            case SUMO:
                return 16;
        }

        return 0;
    }

    public boolean isBuild() {
        switch(this) {
            default:
                return false;
        }
    }

    public boolean isQueueable () {
        switch(this) {
            case NO_DEBUFF:
            case HCF:
            case CLASSIC:
            case BOXING:
            case SUMO:
                return true;
            default:
                return false;
        }
    }

    public boolean isMoreItems() {
        switch(this) {
            case NO_DEBUFF:
                return true;
            default:
                return false;
        }
    }

    public ItemStack[] getMoreItems() {
        switch(this) {
            case NO_DEBUFF:
                ItemStack[] items = this.getGameInventory().getInventory();

                items[3] = new ItemStack(Material.GOLDEN_CARROT, 64);
                return items;
            default:
                break;
        }

        return null;
    }

    public boolean isHunger() {
        switch(this) {
            case BOXING:
            case SUMO:
                return false;
            default:
                return true;
        }
    }

    public boolean isTakeDamage() {
        switch(this) {
            case BOXING:
            case SUMO:
                return false;
            default:
                return true;
        }
    }

    public boolean isMoveOnStart() {
        switch(this) {
            case SUMO:
                return false;
            default:
                return true;
        }
    }

    public boolean isDieInWater() {
        switch(this) {
            case SUMO:
                return true;
            default:
                return false;
        }
    }

    public boolean isRanked() {
        return false;
    }

    public boolean isFfa() {
        switch(this) {
            default:
                return true;
            case HCF:
            case BOXING:
                return false;
        }
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
            case BOXING:
                item = new ItemStack(Material.DIAMOND_CHESTPLATE);
                break;
            case SUMO:
                item = new ItemStack(Material.LEASH);
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

                inv[8] = speed.toItemStack(1);
                inv[17] = speed.toItemStack(1);
                inv[26] = speed.toItemStack(1);
                inv[35] = speed.toItemStack(1);

                inv[7] = fireResistance.toItemStack(1);

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

                inv[8] = speed.toItemStack(1);
                inv[17] = speed.toItemStack(1);
                inv[26] = speed.toItemStack(1);
                inv[35] = speed.toItemStack(1);

                inv[7] = fireResistance.toItemStack(1);

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
            case SUMO:
                inventory.getPotionEffects().add(new PotionEffect(PotionEffectType.JUMP, 99999, 245));
                break;
            case BOXING:
                inv[0] = new ItemStack(Material.DIAMOND_SWORD);
                inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 1);
                inv[0].addEnchantment(Enchantment.DURABILITY, 1);

                inventory.getPotionEffects().add(new PotionEffect(PotionEffectType.SPEED, 99999, 1));
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

        for(PotionEffect effect : gi.getPotionEffects()) {
            player.addPotionEffect(effect);
        }

        pi.setArmorContents(gi.getArmor());
        pi.setContents(gi.getInventory());
    }
}
