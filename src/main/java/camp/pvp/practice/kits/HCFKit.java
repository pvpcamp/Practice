package camp.pvp.practice.kits;

import camp.pvp.practice.games.GameInventory;
import camp.pvp.practice.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public enum HCFKit {
    DIAMOND, BARD, ARCHER;

    @Override
    public String toString() {
        switch(this) {
            case DIAMOND:
                return "Diamond";
            case BARD:
                return "Bard";
            case ARCHER:
                return "Archer";
            default:
                return null;
        }
    }

    public ChatColor getColor() {
        switch(this) {
            case DIAMOND:
                return ChatColor.AQUA;
            case BARD:
                return ChatColor.YELLOW;
            case ARCHER:
                return ChatColor.RED;
            default:
                return ChatColor.WHITE;
        }
    }

    public GameInventory getGameInventory() {
        GameInventory inventory = new GameInventory();
        ItemStack[] armor = inventory.getArmor(), inv = inventory.getInventory();
        switch(this) {
            case DIAMOND:
                armor[3] = new ItemStack(Material.DIAMOND_HELMET);
                armor[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                armor[3].addEnchantment(Enchantment.DURABILITY, 3);

                armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
                armor[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                armor[2].addEnchantment(Enchantment.DURABILITY, 3);

                armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
                armor[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                armor[1].addEnchantment(Enchantment.DURABILITY, 3);

                armor[0] = new ItemStack(Material.DIAMOND_BOOTS);
                armor[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);
                armor[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                armor[0].addEnchantment(Enchantment.DURABILITY, 3);

                inv[0] = new ItemStack(Material.DIAMOND_SWORD);
                inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 1);
                inv[0].addEnchantment(Enchantment.DURABILITY, 3);

                inv[1] = new ItemStack(Material.ENDER_PEARL, 16);
                inv[8] = new ItemStack(Material.COOKED_BEEF, 64);

                Potion speed = new Potion(PotionType.SPEED, 2);

                Potion health = new Potion(PotionType.INSTANT_HEAL, 2);
                health.setSplash(true);

                inv[2] = speed.toItemStack(1);
                inv[17] = speed.toItemStack(1);
                inv[26] = speed.toItemStack(1);
                inv[35] = speed.toItemStack(1);

                for(int x = 0; x < 36; x++) {
                    ItemStack i = inv[x];
                    if(i == null) {
                        inv[x] = health.toItemStack(1);
                    }
                }

                break;
            case BARD:
                armor[3] = new ItemStack(Material.GOLD_HELMET);
                armor[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                armor[3].addEnchantment(Enchantment.DURABILITY, 3);

                armor[2] = new ItemStack(Material.GOLD_CHESTPLATE);
                armor[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                armor[2].addEnchantment(Enchantment.DURABILITY, 3);

                armor[1] = new ItemStack(Material.GOLD_LEGGINGS);
                armor[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                armor[1].addEnchantment(Enchantment.DURABILITY, 3);

                armor[0] = new ItemStack(Material.GOLD_BOOTS);
                armor[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);
                armor[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                armor[0].addEnchantment(Enchantment.DURABILITY, 3);

                inv[0] = new ItemStack(Material.DIAMOND_SWORD);
                inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 1);
                inv[0].addEnchantment(Enchantment.DURABILITY, 3);

                inv[1] = new ItemStack(Material.ENDER_PEARL, 16);
                inv[8] = new ItemStack(Material.COOKED_BEEF, 64);

                inv[2] = new ItemStack(Material.BLAZE_POWDER, 64);
                inv[3] = new ItemStack(Material.GHAST_TEAR, 64);
                inv[4] = new ItemStack(Material.IRON_INGOT, 64);

                inv[9] = new ItemStack(Material.FEATHER, 64);
                inv[10] = new ItemStack(Material.SUGAR, 64);

                health = new Potion(PotionType.INSTANT_HEAL, 2);
                health.setSplash(true);

                for(int x = 0; x < 36; x++) {
                    ItemStack i = inv[x];
                    if(i == null) {
                        inv[x] = health.toItemStack(1);
                    }
                }

                break;
            case ARCHER:
                armor[3] = new ItemStack(Material.LEATHER_HELMET);
                armor[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                armor[3].addEnchantment(Enchantment.DURABILITY, 3);

                armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
                armor[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                armor[2].addEnchantment(Enchantment.DURABILITY, 3);

                armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
                armor[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                armor[1].addEnchantment(Enchantment.DURABILITY, 3);

                armor[0] = new ItemStack(Material.LEATHER_BOOTS);
                armor[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);
                armor[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                armor[0].addEnchantment(Enchantment.DURABILITY, 3);

                inv[0] = new ItemStack(Material.DIAMOND_SWORD);
                inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 1);
                inv[0].addEnchantment(Enchantment.DURABILITY, 3);


                inv[1] = new ItemStack(Material.ENDER_PEARL, 16);

                inv[2] = new ItemStack(Material.BOW, 1);
                inv[2].addEnchantment(Enchantment.ARROW_DAMAGE, 4);
                inv[2].addEnchantment(Enchantment.DURABILITY, 3);
                inv[2].addEnchantment(Enchantment.ARROW_FIRE, 1);
                inv[2].addEnchantment(Enchantment.ARROW_INFINITE, 1);

                inv[3] = new ItemStack(Material.SUGAR, 64);

                inv[8] = new ItemStack(Material.COOKED_BEEF, 64);

                inv[9] = new ItemStack(Material.ARROW, 64);

                health = new Potion(PotionType.INSTANT_HEAL, 2);
                health.setSplash(true);

                for(int x = 0; x < 36; x++) {
                    ItemStack i = inv[x];
                    if(i == null) {
                        inv[x] = health.toItemStack(1);
                    }
                }
                break;
            default:
                break;

        }

        return inventory;
    }

    public void apply(Player player) {
        PlayerInventory pi = player.getInventory();
        GameInventory gi = this.getGameInventory();

        PlayerUtils.reset(player, false);

        applyEffects(player);

        pi.setArmorContents(gi.getArmor());
        pi.setContents(gi.getInventory());
        player.updateInventory();
    }

    public void applyEffects(Player player) {
        for(PotionEffect pe : player.getActivePotionEffects()) {
            if(pe.getDuration() > 10000) {
                player.removePotionEffect(pe.getType());
            }
        }

        PotionEffect fireResistance = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999999, 0);

        switch(this) {
            case BARD:
                PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 1);
                PotionEffect regeneration = new PotionEffect(PotionEffectType.REGENERATION, 999999, 0);
                PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 999999, 1);

                player.addPotionEffect(resistance);
                player.addPotionEffect(regeneration);
                player.addPotionEffect(speed);
                player.addPotionEffect(fireResistance);
                break;
            case ARCHER:
                resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 1);
                speed = new PotionEffect(PotionEffectType.SPEED, 999999, 2);

                player.addPotionEffect(resistance);
                player.addPotionEffect(speed);
                player.addPotionEffect(fireResistance);
                break;
            case DIAMOND:
                player.addPotionEffect(fireResistance);
                break;
        }
    }
}
