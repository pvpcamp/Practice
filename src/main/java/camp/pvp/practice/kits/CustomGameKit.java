package camp.pvp.practice.kits;

import camp.pvp.practice.games.GameInventory;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.GameTeam;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.items.ItemStackDeserializer;
import camp.pvp.practice.utils.items.ItemStackSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class CustomGameKit {

    private final GameKit gameKit;
    private final int slot;
    private ItemStack[] items;
    private String name;

    public CustomGameKit(GameKit gameKit, int slot, boolean existing) {
        this.gameKit = gameKit;
        this.slot = slot;
        this.name = "&f" + gameKit.getDisplayName() + " Kit " + slot;

        if (existing) {
            items = new ItemStack[36];
        } else {
            this.items = gameKit.getGameInventory().getInventory();
        }
    }

    public void importFromMap(Map<String, Object> map) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
                .registerTypeAdapter(ItemStack.class, new ItemStackDeserializer())
                .create();

        this.name = (String) map.get("name");

        for(Map.Entry<String, String> entry : ((Map<String, String>) map.get("items")).entrySet()) {
            int i = Integer.parseInt(entry.getKey());
            items[i] = gson.fromJson(entry.getValue(), ItemStack.class);
        }
    }

    public Map<String, Object> exportItems() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
                .registerTypeAdapter(ItemStack.class, new ItemStackDeserializer())
                .create();

        Map<String, Object> map = new HashMap<>();

        map.put("name", this.getName());

        Map<String, String> items = new HashMap<>();
        for(int x = 0; x < 36; x++) {
            ItemStack i = getItems()[x];
            if(i != null && !i.getType().equals(Material.AIR)) {
                items.put(String.valueOf(x), gson.toJson(i, ItemStack.class));
            }
        }

        map.put("items", items);

        return map;
    }

    public void setItems(ItemStack[] items) {
        for(ItemStack i : items) {
            if (i == null) continue;
            if (i.getType().equals(Material.AIR)) continue;
            if (i.getAmount() == 0) i.setAmount(1);
        }

        this.items = items;
    }

    public void apply(Player player) {
        PlayerInventory pi = player.getInventory();
        GameInventory gi = gameKit.getGameInventory();

        PlayerUtils.reset(player, false);

        for(PotionEffect effect : gi.getPotionEffects()) {
            player.addPotionEffect(effect);
        }

        pi.setArmorContents(gi.getArmor());
        pi.setContents(this.items);
        player.updateInventory();
    }

    public void apply(GameParticipant participant) {
        Player player = participant.getPlayer();
        PlayerInventory pi = player.getInventory();
        GameInventory gi = gameKit.getGameInventory();

        PlayerUtils.reset(player, false);

        for(PotionEffect effect : gi.getPotionEffects()) {
            player.addPotionEffect(effect);
        }

        pi.setArmorContents(gi.getArmor());
        pi.setContents(this.items);

        participant.setKitApplied(true);

        if(gameKit.equals(GameKit.BED_FIGHT)) {
            ItemStack[] armor = pi.getArmorContents();
            GameTeam.Color color = participant.getTeamColor();

            LeatherArmorMeta helmetMeta = (LeatherArmorMeta) armor[3].getItemMeta();
            LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) armor[2].getItemMeta();
            LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) armor[1].getItemMeta();
            LeatherArmorMeta bootsMeta = (LeatherArmorMeta) armor[0].getItemMeta();

            if(color.equals(GameTeam.Color.BLUE)) {
                helmetMeta.setColor(Color.BLUE);
                chestplateMeta.setColor(Color.BLUE);
                leggingsMeta.setColor(Color.BLUE);
                bootsMeta.setColor(Color.BLUE);

                for(ItemStack item : pi.getContents()) {
                    if(item == null) continue;
                    if(!item.getType().equals(Material.WOOL)) continue;

                    item.setDurability((short) 11);
                }
            } else {
                helmetMeta.setColor(Color.RED);
                chestplateMeta.setColor(Color.RED);
                leggingsMeta.setColor(Color.RED);
                bootsMeta.setColor(Color.RED);

                for(ItemStack item : pi.getContents()) {
                    if(item == null) continue;
                    if(!item.getType().equals(Material.WOOL)) continue;

                    item.setDurability((short) 14);
                }
            }

            armor[3].setItemMeta(helmetMeta);
            armor[2].setItemMeta(chestplateMeta);
            armor[1].setItemMeta(leggingsMeta);
            armor[0].setItemMeta(bootsMeta);
        }

        if(!gameKit.isItemDurability()) {

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
