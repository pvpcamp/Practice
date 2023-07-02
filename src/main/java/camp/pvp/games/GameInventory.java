package camp.pvp.games;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GameInventory {

    private @Getter @Setter ItemStack[] armor, inventory;

    public GameInventory() {
        armor = new ItemStack[4];
        inventory = new ItemStack[36];
    }
}
