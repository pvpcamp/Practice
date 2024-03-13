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
            this.items = gameKit.getBaseKit().getItems();
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

    @Deprecated
    public void apply(Player player) {
        gameKit.getBaseKit().apply(player, this);
    }

    @Deprecated
    public void apply(GameParticipant participant) {
        gameKit.getBaseKit().apply(participant, this);
    }
}
