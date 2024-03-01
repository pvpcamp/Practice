package camp.pvp.practice.kits;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.games.GameInventory;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.GameTeam;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.PlayerUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum GameKit {
    NO_DEBUFF, BOXING, BED_FIGHT, SUMO, DEBUFF, ARCHER, BUILD_UHC, CLASSIC, SOUP, HCF, INVADED, SKYWARS, SPLEEF, STRATEGY, ONE_IN_THE_CHAMBER;

    public String getDisplayName() {
        switch(this) {
            case BUILD_UHC:
                return "Build UHC";
            case HCF:
                return "HCF";
            default:
                String name = this.name();
                name = name.replace("_", " ");
                return WordUtils.capitalizeFully(name);
        }
    }

    public List<Arena.Type> getArenaTypes() {
        switch(this) {
            case HCF:
                return Arrays.asList(Arena.Type.DUEL_HCF);
            case SUMO:
                return Collections.singletonList(Arena.Type.DUEL_SUMO);
            case BOXING:
                return Collections.singletonList(Arena.Type.DUEL_FLAT);
            case BUILD_UHC:
            case STRATEGY:
                return Collections.singletonList(Arena.Type.DUEL_BUILD);
            case SKYWARS:
                return Collections.singletonList(Arena.Type.DUEL_SKYWARS);
            case SPLEEF:
                return Collections.singletonList(Arena.Type.SPLEEF);
            case BED_FIGHT:
                return Collections.singletonList(Arena.Type.DUEL_BED_FIGHT);
            case ONE_IN_THE_CHAMBER:
                return Collections.singletonList(Arena.Type.MINIGAME_OITC);
            default:
                return Arrays.asList(Arena.Type.DUEL, Arena.Type.DUEL_FLAT);
        }
    }

    public boolean isBuild() {
        switch(this) {
            case BUILD_UHC:
            case SKYWARS:
            case BED_FIGHT:
            case SPLEEF:
            case STRATEGY:
                return true;
            default:
                return false;
        }
    }

    public boolean isRespawn() {
        return this.equals(BED_FIGHT) || this.equals(ONE_IN_THE_CHAMBER);
    }

    public boolean isRegen() {
        switch(this) {
            case BUILD_UHC:
                return false;
            default:
                return true;
        }
    }

    public boolean isDuelKit() {
        return !this.equals(ONE_IN_THE_CHAMBER);
    }

    public boolean isRanked() {
        return !this.equals(HCF);
    }

    public boolean isTournament() {
        switch(this) {
            case HCF:
            case SUMO:
                return false;
            default:
                return true;
        }
    }

    public boolean isEditable() {
        switch(this) {
            case SUMO:
            case SPLEEF:
                return false;
            default:
                return true;
        }
    }

    public ItemStack[] getMoreItems() {
        switch(this) {
            case NO_DEBUFF:
                ItemStack[] items = this.getGameInventory().getInventory();

                items[7] = new ItemStack(Material.GOLDEN_CARROT, 64);
                return items;
            default:
                break;
        }

        return null;
    }

    public boolean isHunger() {
        switch(this) {
            case SOUP:
            case BOXING:
            case SUMO:
            case INVADED:
            case SPLEEF:
            case BED_FIGHT:
            case ONE_IN_THE_CHAMBER:
            case ARCHER:
                return false;
            default:
                return true;
        }
    }

    public boolean showHealthBar() {
        switch(this) {
            case BED_FIGHT:
            case BUILD_UHC:
            case STRATEGY:
            case INVADED:
            case CLASSIC:
            case ONE_IN_THE_CHAMBER:
            case SKYWARS:
            case ARCHER:
                return true;
            default:
                return false;
        }
    }

    public boolean isTakeDamage() {
        switch(this) {
            case BOXING:
            case SUMO:
            case SPLEEF:
                return false;
            default:
                return true;
        }
    }

    public boolean isMoveOnStart() {
        switch(this) {
            case BED_FIGHT:
            case SUMO:
            case SKYWARS:
            case ONE_IN_THE_CHAMBER:
                return false;
            default:
                return true;
        }
    }

    public boolean isDieInWater() {
        switch(this) {
            case SUMO:
            case SPLEEF:
                return true;
            default:
                return false;
        }
    }

    public boolean isFfa() {
        switch(this) {
            default:
                return true;
            case HCF:
            case BOXING:
            case SKYWARS:
            case BED_FIGHT:
                return false;
        }
    }

    public boolean isTeams() {
        switch(this){
            case BOXING:
                return false;
            default:
                return true;
        }
    }

    public boolean isIssueCooldowns() {
        switch(this){
            case SKYWARS:
            case STRATEGY:
                return false;
            default:
                return true;
        }
    }

    public boolean isDropItemsOnDeath() {
        switch(this) {
            case BED_FIGHT:
            case SUMO:
            case SPLEEF:
            case ONE_IN_THE_CHAMBER:
            case ARCHER:
                return false;
            default:
                return true;
        }
    }

    public boolean isItemDurability() {
        switch(this) {
            case BED_FIGHT:
            case SUMO:
            case SPLEEF:
            case ONE_IN_THE_CHAMBER:
            case ARCHER:
                return false;
            default:
                return true;
        }
    }

    public boolean isArrowOneShot() {
        return this.equals(ONE_IN_THE_CHAMBER);
    }

    public boolean isArrowPickup() {
        switch(this) {
            case ARCHER, ONE_IN_THE_CHAMBER:
                return false;
            default:
                return true;
        }
    }

    public boolean isFallDamage() {
        switch (this) {
            case BED_FIGHT, ONE_IN_THE_CHAMBER, SUMO -> {
                return false;
            }
            default -> {
                return true;
            }
        }
    }

    public boolean isShowArrowDamage() {
        return !this.equals(ONE_IN_THE_CHAMBER);
    }

    public ItemStack getIcon() {
        ItemStack item = new ItemStack(Material.GLASS);
        switch(this) {
            case NO_DEBUFF:
                Potion potion = new Potion(PotionType.INSTANT_HEAL);
                potion.setSplash(true);
                item = potion.toItemStack(1);
                break;
            case BUILD_UHC:
                item = new ItemStack(Material.LAVA_BUCKET);
                break;
            case HCF:
                item = new ItemStack(Material.FENCE);
                break;
            case DEBUFF:
                potion = new Potion(PotionType.POISON);
                potion.setSplash(true);
                item = potion.toItemStack(1);
                break;
            case ARCHER:
                item = new ItemStack(Material.BOW);
                break;
            case CLASSIC:
                item = new ItemStack(Material.DIAMOND_SWORD);
                break;
            case SOUP:
                item = new ItemStack(Material.MUSHROOM_SOUP);
                break;
            case INVADED:
                item = new ItemStack(Material.BLAZE_POWDER);
                break;
            case SKYWARS:
                item = new ItemStack(Material.EYE_OF_ENDER);
                break;
            case SPLEEF:
                item = new ItemStack(Material.SNOW_BALL);
                break;
            case BOXING:
                item = new ItemStack(Material.DIAMOND_CHESTPLATE);
                break;
            case SUMO:
                item = new ItemStack(Material.LEASH);
                break;
            case STRATEGY:
                item = new ItemStack(Material.WEB);
                break;
            case BED_FIGHT:
                item = new ItemStack(Material.BED);
                break;
            case ONE_IN_THE_CHAMBER:
                item = new ItemStack(Material.ARROW);
                break;
        }

        return item;
    }

    public GameInventory getGameInventory() {
        GameInventory inventory = new GameInventory();
        ItemStack[] armor = inventory.getArmor(), inv = inventory.getInventory();
        switch(this) {
            case DEBUFF:
                Potion poison = new Potion(PotionType.POISON, 1);
                poison.setSplash(true);

                Potion slowness = new Potion(PotionType.SLOWNESS, 1);
                slowness.setSplash(true);

                inv[9] = poison.toItemStack(1);
                inv[10] = slowness.toItemStack(1);
                inv[18] = poison.toItemStack(1);
                inv[19] = slowness.toItemStack(1);
                inv[27] = poison.toItemStack(1);
                inv[28] = slowness.toItemStack(1);
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
                inv[8] = new ItemStack(Material.COOKED_BEEF, 64);

                Potion speed = new Potion(PotionType.SPEED, 2);

                Potion fireResistance = new Potion(PotionType.FIRE_RESISTANCE, 1);
                fireResistance.setHasExtendedDuration(true);

                Potion health = new Potion(PotionType.INSTANT_HEAL, 2);
                health.setSplash(true);

                inv[2] = speed.toItemStack(1);
                inv[17] = speed.toItemStack(1);
                inv[26] = speed.toItemStack(1);
                inv[35] = speed.toItemStack(1);

                inv[3] = fireResistance.toItemStack(1);

                for(int x = 0; x < 36; x++) {
                    ItemStack i = inv[x];
                    if(i == null) {
                        inv[x] = health.toItemStack(1);
                    }
                }

                break;
            case BUILD_UHC:
                armor[3] = new ItemStack(Material.DIAMOND_HELMET);
                armor[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

                armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
                armor[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

                armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
                armor[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

                armor[0] = new ItemStack(Material.DIAMOND_BOOTS);
                armor[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

                inv[0] = new ItemStack(Material.DIAMOND_SWORD);
                inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 3);
                inv[1] = new ItemStack(Material.FISHING_ROD, 1);
                inv[2] = new ItemStack(Material.BOW);
                inv[2].addEnchantment(Enchantment.ARROW_DAMAGE, 3);
                inv[3] = new ItemStack(Material.COOKED_BEEF, 64);
                inv[4] = new ItemStack(Material.GOLDEN_APPLE, 6);
                inv[5] = new ItemStack(Material.GOLDEN_APPLE, 3);
                ItemMeta headMeta = inv[5].getItemMeta();
                headMeta.setDisplayName(Colors.get("&6Golden Head"));
                inv[5].setItemMeta(headMeta);

                inv[6] = new ItemStack(Material.DIAMOND_PICKAXE);
                inv[7] = new ItemStack(Material.DIAMOND_AXE);
                inv[8] = new ItemStack(Material.WOOD, 64);
                inv[9] = new ItemStack(Material.ARROW, 20);
                inv[10] = new ItemStack(Material.COBBLESTONE, 64);
                inv[11] = new ItemStack(Material.WATER_BUCKET);
                inv[12] = new ItemStack(Material.WATER_BUCKET);
                inv[13] = new ItemStack(Material.LAVA_BUCKET);
                inv[14] = new ItemStack(Material.LAVA_BUCKET);
                break;
            case SKYWARS:
                armor[3] = new ItemStack(Material.IRON_HELMET);
                armor[2] = new ItemStack(Material.IRON_CHESTPLATE);
                armor[1] = new ItemStack(Material.IRON_LEGGINGS);
                armor[0] = new ItemStack(Material.IRON_BOOTS);

                inv[0] = new ItemStack(Material.IRON_SWORD);
                inv[1] = new ItemStack(Material.IRON_PICKAXE);
                inv[2] = new ItemStack(Material.IRON_AXE);

                break;
            case ARCHER:
                armor[3] = new ItemStack(Material.LEATHER_HELMET);
                armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
                armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
                armor[0] = new ItemStack(Material.LEATHER_BOOTS);

                inv[0] = new ItemStack(Material.BOW);
                inv[0].addEnchantment(Enchantment.ARROW_INFINITE, 1);

                Potion nightVision = new Potion(PotionType.NIGHT_VISION, 1);
                nightVision.setHasExtendedDuration(true);
                inv[1] = nightVision.toItemStack(1);

                inv[9] = new ItemStack(Material.ARROW);
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
            case SOUP:
                armor[3] = new ItemStack(Material.IRON_HELMET);
                armor[2] = new ItemStack(Material.IRON_CHESTPLATE);
                armor[1] = new ItemStack(Material.IRON_LEGGINGS);
                armor[0] = new ItemStack(Material.IRON_BOOTS);

                inv[0] = new ItemStack(Material.DIAMOND_SWORD);

                for(int x = 0; x < 36; x++) {
                    ItemStack i = inv[x];
                    if(i == null) {
                        inv[x] = new ItemStack(Material.MUSHROOM_SOUP);
                    }
                }
                break;
            case INVADED:
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
                inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 3);
                inv[0].addEnchantment(Enchantment.DURABILITY, 3);

                inv[1] = new ItemStack(Material.BOW, 1);
                inv[1].addEnchantment(Enchantment.ARROW_DAMAGE, 2);
                inv[1].addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
                inv[1].addEnchantment(Enchantment.ARROW_INFINITE, 1);

                inv[2] = new ItemStack(Material.FISHING_ROD, 1);
                inv[3] = new ItemStack(Material.GOLDEN_APPLE, 64);
                inv[4] = new ItemStack(Material.GOLDEN_APPLE, 2);
                inv[4].setDurability((short) 1);

                Potion instantDamage = new Potion(PotionType.INSTANT_DAMAGE, 1);
                instantDamage.setSplash(true);
                inv[5] = instantDamage.toItemStack(1);

                inv[6] = new ItemStack(Material.ENDER_PEARL);

                speed = new Potion(PotionType.SPEED, 1);
                speed.setHasExtendedDuration(true);
                inv[7] = speed.toItemStack(1);

                inv[9] = new ItemStack(Material.ARROW, 1);
                break;
            case SUMO:
                PotionEffect jump = new PotionEffect(PotionEffectType.JUMP, 99999, 245, true, false);
                inventory.getPotionEffects().add(jump);
                break;
            case BOXING:
                inv[0] = new ItemStack(Material.DIAMOND_SWORD);
                inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 1);
                inv[0].addEnchantment(Enchantment.DURABILITY, 1);

                inventory.getPotionEffects().add(new PotionEffect(PotionEffectType.SPEED, 99999, 1));
                break;
            case SPLEEF:
                inv[0] = new ItemStack(Material.DIAMOND_SPADE);
                inv[0].addEnchantment(Enchantment.DIG_SPEED, 5);
                inv[0].addEnchantment(Enchantment.DURABILITY, 3);

                break;
            case STRATEGY:
                armor[3] = new ItemStack(Material.DIAMOND_HELMET);
                armor[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                armor[3].addEnchantment(Enchantment.DURABILITY, 3);

                armor[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
                armor[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                armor[2].addEnchantment(Enchantment.DURABILITY, 3);

                armor[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
                armor[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                armor[1].addEnchantment(Enchantment.DURABILITY, 3);

                armor[0] = new ItemStack(Material.DIAMOND_BOOTS);
                armor[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                armor[0].addEnchantment(Enchantment.DURABILITY, 3);

                inv[0] = new ItemStack(Material.DIAMOND_AXE);
                inv[0].addEnchantment(Enchantment.DAMAGE_ALL, 3);
                inv[0].addEnchantment(Enchantment.DURABILITY, 3);

                inv[1] = new ItemStack(Material.FISHING_ROD);
                inv[1].addEnchantment(Enchantment.DURABILITY, 3);

                inv[2] = new ItemStack(Material.BOW);
                inv[2].addEnchantment(Enchantment.ARROW_DAMAGE, 2);
                inv[2].addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);

                inv[3] = new ItemStack(Material.WOOD, 64);
                inv[4] = new ItemStack(Material.ENDER_PEARL, 2);

                slowness = new Potion(PotionType.SLOWNESS, 1);
                slowness.setSplash(true);

                inv[5] = slowness.toItemStack(1);

                inv[6] = new ItemStack(Material.WEB, 64);
                inv[7] = new ItemStack(Material.GOLDEN_APPLE, 8);

                inv[9] = new ItemStack(Material.SNOW_BALL, 16);
                inv[10] = new ItemStack(Material.LAVA_BUCKET);
                inv[11] = new ItemStack(Material.LAVA_BUCKET);
                inv[12] = new ItemStack(Material.WATER_BUCKET);
                inv[13] = new ItemStack(Material.WATER_BUCKET);
                inv[14] = new ItemStack(Material.ARROW, 16);

                speed = new Potion(PotionType.SPEED, 2);

                inv[8] = speed.toItemStack(1);
                inv[17] = speed.toItemStack(1);
                inv[26] = speed.toItemStack(1);
                inv[35] = speed.toItemStack(1);

                break;
            case BED_FIGHT:
                armor[3] = new ItemStack(Material.LEATHER_HELMET);

                armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);

                armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);

                armor[0] = new ItemStack(Material.LEATHER_BOOTS);

                inv[0] = new ItemStack(Material.WOOD_SWORD);

                inv[1] = new ItemStack(Material.WOOD_PICKAXE);
                inv[1].addEnchantment(Enchantment.DIG_SPEED, 1);

                inv[2] = new ItemStack(Material.WOOD_AXE);
                inv[2].addEnchantment(Enchantment.DIG_SPEED, 1);

                inv[3] = new ItemStack(Material.SHEARS);
                inv[4] = new ItemStack(Material.WOOL, 64);
                break;
            case ONE_IN_THE_CHAMBER:
                inv[0] = new ItemStack(Material.WOOD_SWORD);
                inv[1] = new ItemStack(Material.BOW);
                inv[2] = new ItemStack(Material.ARROW, 1);
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

        for(PotionEffect effect : gi.getPotionEffects()) {
            player.addPotionEffect(effect);
        }

        pi.setArmorContents(gi.getArmor());
        pi.setContents(gi.getInventory());
        player.updateInventory();
    }

    public void apply(GameParticipant participant) {
        Player player = participant.getPlayer();
        PlayerInventory pi = player.getInventory();
        GameInventory gi = this.getGameInventory();

        PlayerUtils.reset(player, false);

        for(PotionEffect effect : gi.getPotionEffects()) {
            player.addPotionEffect(effect);
        }

        pi.setArmorContents(gi.getArmor());
        pi.setContents(gi.getInventory());

        participant.setKitApplied(true);

        if(this.equals(BED_FIGHT)) {
            ItemStack[] armor = pi.getArmorContents();
            GameTeam.Color color = participant.getTeamColor();

            LeatherArmorMeta helmetMeta = (LeatherArmorMeta) armor[3].getItemMeta();
            LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) armor[2].getItemMeta();
            LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) armor[1].getItemMeta();
            LeatherArmorMeta bootsMeta = (LeatherArmorMeta) armor[0].getItemMeta();

            if (color.equals(GameTeam.Color.BLUE)) {
                helmetMeta.setColor(Color.BLUE);
                chestplateMeta.setColor(Color.BLUE);
                leggingsMeta.setColor(Color.BLUE);
                bootsMeta.setColor(Color.BLUE);

                for (ItemStack item : pi.getContents()) {
                    if (item == null) continue;
                    if (!item.getType().equals(Material.WOOL)) continue;

                    item.setDurability((short) 11);
                }
            } else {
                helmetMeta.setColor(Color.RED);
                chestplateMeta.setColor(Color.RED);
                leggingsMeta.setColor(Color.RED);
                bootsMeta.setColor(Color.RED);

                for (ItemStack item : pi.getContents()) {
                    if (item == null) continue;
                    if (!item.getType().equals(Material.WOOL)) continue;

                    item.setDurability((short) 14);
                }
            }

            armor[3].setItemMeta(helmetMeta);
            armor[2].setItemMeta(chestplateMeta);
            armor[1].setItemMeta(leggingsMeta);
            armor[0].setItemMeta(bootsMeta);
        }

        if(!this.isItemDurability()) {

            for (ItemStack item : pi.getContents()) {

                if(item == null) continue;

                ItemMeta meta = item.getItemMeta();

                if(meta == null) continue;

                meta.spigot().setUnbreakable(true);
                item.setItemMeta(meta);
            }

            for (ItemStack item : pi.getArmorContents()) {

                if(item == null) continue;

                ItemMeta meta = item.getItemMeta();

                if(meta == null) continue;

                meta.spigot().setUnbreakable(true);
                item.setItemMeta(meta);
            }
        }

        player.updateInventory();
    }
}
