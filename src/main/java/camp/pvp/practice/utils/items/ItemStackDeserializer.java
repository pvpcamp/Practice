package camp.pvp.practice.utils.items;

import com.google.gson.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.Map;

public class ItemStackDeserializer implements JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Material material = Material.valueOf(jsonObject.get("type").getAsString());
        int amount = jsonObject.get("amount").getAsInt();
        short durability = jsonObject.get("durability").getAsShort();

        ItemStack itemStack = new ItemStack(material, amount, durability);

        // Deserialize enchantments
        JsonObject enchantmentsObject = jsonObject.getAsJsonObject("enchantments");
        for (Map.Entry<String, JsonElement> entry : enchantmentsObject.entrySet()) {
            Enchantment enchantment = Enchantment.getByName(entry.getKey());
            int level = entry.getValue().getAsInt();
            itemStack.addEnchantment(enchantment, level);
        }

        return itemStack;
    }
}
