package camp.pvp.practice.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

public class ItemUtils {

    private static JSONParser parser;

    static {
        parser = new JSONParser();
    }

    public static String convert(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        JSONObject json = new JSONObject();
        Map<Integer, Integer> enchantments = new HashMap<>();

        for(Map.Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
            enchantments.put(entry.getKey().getId(), entry.getValue());
        }

        json.put("material", item.getType().toString());
        json.put("durability", item.getDurability());
        json.put("amount", item.getAmount());

        if(meta.getDisplayName() != null) {
            json.put("name", meta.getDisplayName());
        }

        json.put("enchantments", enchantments);
        return json.toString();
    }

    public static ItemStack convert(String s) {
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(json != null) {
            ItemStack item = new ItemStack(Material.valueOf((String) json.get("material")));
            item.setDurability((short) ((long) json.get("durability")));
            item.setAmount((int) ((long) json.get("amount")));

            if (json.get("name") != null) {
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', (String) json.get("name")));
                item.setItemMeta(meta);
            }

            if (json.get("enchantments") != null) {
                Map<String, Long> map = (Map<String, Long>) json.get("enchantments");
                for (Map.Entry<String, Long> entry : map.entrySet()) {
                    item.addEnchantment(Enchantment.getById(Integer.parseInt(entry.getKey())), entry.getValue().intValue());
                }
            }

            return item;
        }

        return null;
    }
}
