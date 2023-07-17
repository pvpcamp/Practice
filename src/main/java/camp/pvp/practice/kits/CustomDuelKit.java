package camp.pvp.practice.kits;

import camp.pvp.practice.games.GameInventory;
import camp.pvp.practice.utils.ItemUtils;
import camp.pvp.practice.utils.PlayerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
        this.name = (String) map.get("name");

        for(Map.Entry<String, Object> entry : ((Map<String, Object>) map.get("items")).entrySet()) {
            int i = Integer.parseInt(entry.getKey());
            items[i] = ItemUtils.convert((String) entry.getValue());
        }
    }

    public Map<String, Object> exportItems() {
        Map<String, Object> map = new HashMap<>();

        map.put("name", getName());

        Map<String, String> items = new HashMap<>();
        for(int x = 0; x < 36; x++) {
            ItemStack i = getItems()[x];
            if(i != null && !i.getType().equals(Material.AIR)) {
                items.put(String.valueOf(x), ItemUtils.convert(getItems()[x]));
            }
        }

        map.put("items", items);

        return map;
    }

    public void apply(Player player) {
        PlayerInventory pi = player.getInventory();
        GameInventory gi = duelKit.getGameInventory();

        PlayerUtils.reset(player);

        pi.setArmorContents(gi.getArmor());
        pi.setContents(this.items);
    }
}
