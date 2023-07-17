package camp.pvp.practice.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder extends ItemStack {

    private ItemMeta meta;

    public ItemBuilder(Material material) {
        super(material, 1);
        meta = this.getItemMeta();
    }

    public ItemBuilder(Material material, String displayName) {
        this(material);
        setName(displayName);
    }

    public ItemBuilder setName(String name) {
        meta.setDisplayName(Colors.get(name));
        return this;
    }

    public ItemBuilder setLore(String... strings) {
        List<String> lore = new ArrayList<>();
        for(String s : strings) {
            lore.add(Colors.get(s));
        }
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder unbreakable(boolean b) {
        this.meta.spigot().setUnbreakable(b);
        return this;
    }

    public ItemStack create() {
        this.setItemMeta(meta);
        return this;
    }
}
