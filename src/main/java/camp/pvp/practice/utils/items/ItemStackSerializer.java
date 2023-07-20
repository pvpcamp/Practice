package camp.pvp.practice.utils.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;


public class ItemStackSerializer implements JsonSerializer<ItemStack> {

    @Override
    public JsonElement serialize(ItemStack itemStack, java.lang.reflect.Type type, JsonSerializationContext context) {
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

        return jsonObject;
    }
}
