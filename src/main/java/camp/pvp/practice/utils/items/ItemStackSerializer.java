package camp.pvp.practice.utils.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;


public class ItemStackSerializer implements JsonSerializer<ItemStack> {

    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", itemStack.getType().name());
        jsonObject.addProperty("amount", itemStack.getAmount());
        jsonObject.addProperty("durability", itemStack.getDurability());

        // Serialize enchantments
        JsonObject enchantmentsObject = new JsonObject();
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            int level = itemStack.getEnchantmentLevel(enchantment);
            enchantmentsObject.addProperty(enchantment.getName(), level);
        }
        jsonObject.add("enchantments", enchantmentsObject);

        ItemMeta meta = itemStack.getItemMeta();
        if(meta.getDisplayName() != null) {
            jsonObject.addProperty("display_name", meta.getDisplayName());
        }

        return jsonObject;
    }
}
