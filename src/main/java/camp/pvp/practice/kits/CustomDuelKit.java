package camp.pvp.practice.kits;

import camp.pvp.practice.games.GameInventory;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.items.ItemStackDeserializer;
import camp.pvp.practice.utils.items.ItemStackSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class CustomDuelKit {

    private final DuelKit duelKit;
    private final int slot;
    private ItemStack[] items;
    private String name;

    public CustomDuelKit(DuelKit duelKit, int slot, boolean existing) {
        this.duelKit = duelKit;
        this.slot = slot;
        this.name = duelKit.getColor() + duelKit.getDisplayName() + " Kit " + slot;

        if (existing) {
            items = new ItemStack[36];
        } else {
            this.items = duelKit.getGameInventory().getInventory();
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

    public void apply(Player player) {
        PlayerInventory pi = player.getInventory();
        GameInventory gi = duelKit.getGameInventory();

        PlayerUtils.reset(player);

        for(PotionEffect effect : gi.getPotionEffects()) {
            player.addPotionEffect(effect);
        }

        pi.setArmorContents(gi.getArmor());
        pi.setContents(this.items);


    }
}
