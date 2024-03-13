package camp.pvp.practice.kits;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.GameTeam;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.utils.PlayerUtils;
import lombok.Data;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public abstract class BaseKit {

    private final ItemStack[] items, armor, moreItems;
    private final List<PotionEffect> potionEffects;
    private GameKit gameKit;
    private ItemStack icon;
    private List<Arena.Type> arenaTypes;
    private List<GameQueue.GameType> gameTypes;
    private boolean build, respawn, regen, ranked, tournament, editable, hunger, showHealthBar, takeDamage,
            placeTntBeforeStart, placeBlocksBeforeStart, moveOnStart, dieInWater, ffa, teams, issueCooldowns,
            dropItemsOnDeath, itemDurability, arrowOneShot, arrowPickup, fallDamage, cappedBlockHits, showArrowDamage,
            applyLeatherTeamColor, bedwars, boxing, biggerExplosions;

    public BaseKit(GameKit gameKit) {
        this.items = new ItemStack[36];
        this.moreItems = new ItemStack[36];
        this.armor = new ItemStack[4];
        this.gameTypes = new ArrayList<>();
        this.potionEffects = new ArrayList<>();
        this.gameKit = gameKit;

        this.icon = new ItemStack(Material.GLASS);

        this.arenaTypes = Arrays.asList(Arena.Type.DUEL, Arena.Type.DUEL_FLAT);

        this.regen = true;
        this.editable = true;
        this.hunger = true;
        this.takeDamage = true;
        this.moveOnStart = true;
        this.issueCooldowns = true;
        this.dropItemsOnDeath = true;
        this.itemDurability = true;
        this.arrowPickup = true;
        this.fallDamage = true;
        this.showArrowDamage = true;
    }

    public void apply(Player player) {
        apply(player, null);
    }

    public void apply(Player player, CustomGameKit customKit) {
        PlayerInventory pi = player.getInventory();
        ItemStack[] items = getItems();

        if(customKit != null) {
            items = customKit.getItems();
        }

        PlayerUtils.reset(player, false);

        pi.setContents(items.clone());
        player.updateInventory();
    }

    public void apply(GameParticipant participant) {
        apply(participant, null);
    }

    public void apply(GameParticipant participant, CustomGameKit customKit) {
        Player player = participant.getPlayer();
        PlayerInventory pi = player.getInventory();
        ItemStack[] items = getItems();

        if(customKit != null) {
            items = customKit.getItems();
        }

        PlayerUtils.reset(player, false);

        for(PotionEffect effect : getPotionEffects()) {
            player.addPotionEffect(effect);
        }

        pi.setArmorContents(getArmor().clone());
        pi.setContents(items.clone());

        participant.setKitApplied(true);

        if(isApplyLeatherTeamColor()) {
            ItemStack[] armor = pi.getArmorContents();
            GameTeam.Color color = participant.getTeamColor();

            LeatherArmorMeta helmetMeta = (LeatherArmorMeta) armor[3].getItemMeta();
            LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) armor[2].getItemMeta();
            LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) armor[1].getItemMeta();
            LeatherArmorMeta bootsMeta = (LeatherArmorMeta) armor[0].getItemMeta();

            switch(color) {
                case BLUE:
                    helmetMeta.setColor(Color.BLUE);
                    chestplateMeta.setColor(Color.BLUE);
                    leggingsMeta.setColor(Color.BLUE);
                    bootsMeta.setColor(Color.BLUE);

                    for(ItemStack item : pi.getContents()) {
                        if(item == null) continue;
                        if(!item.getType().equals(Material.WOOL)) continue;

                        item.setDurability((short) 11);
                    }
                    break;
                case RED:
                    helmetMeta.setColor(Color.RED);
                    chestplateMeta.setColor(Color.RED);
                    leggingsMeta.setColor(Color.RED);
                    bootsMeta.setColor(Color.RED);

                    for(ItemStack item : pi.getContents()) {
                        if(item == null) continue;
                        if(!item.getType().equals(Material.WOOL)) continue;

                        item.setDurability((short) 14);
                    }
                    break;
                case YELLOW:
                    helmetMeta.setColor(Color.YELLOW);
                    chestplateMeta.setColor(Color.YELLOW);
                    leggingsMeta.setColor(Color.YELLOW);
                    bootsMeta.setColor(Color.YELLOW);

                    for(ItemStack item : pi.getContents()) {
                        if(item == null) continue;
                        if(!item.getType().equals(Material.WOOL)) continue;

                        item.setDurability((short) 4);
                    }
                    break;
                case WHITE:
                    helmetMeta.setColor(Color.WHITE);
                    chestplateMeta.setColor(Color.WHITE);
                    leggingsMeta.setColor(Color.WHITE);
                    bootsMeta.setColor(Color.WHITE);
                    break;
            }

            armor[3].setItemMeta(helmetMeta);
            armor[2].setItemMeta(chestplateMeta);
            armor[1].setItemMeta(leggingsMeta);
            armor[0].setItemMeta(bootsMeta);
        }

        if(!this.isItemDurability()) {

            for (ItemStack item : pi.getContents()) {

                if(item == null || item.getType().isBlock()) continue;

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
