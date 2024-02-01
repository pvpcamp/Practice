package camp.pvp.practice.games;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class GameInventory {

    private ItemStack[] armor, inventory;
    private List<PotionEffect> potionEffects;

    public GameInventory() {
        armor = new ItemStack[4];
        inventory = new ItemStack[36];
        potionEffects = new ArrayList<>();
    }
}
