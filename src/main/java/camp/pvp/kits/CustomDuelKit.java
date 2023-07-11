package camp.pvp.kits;

import camp.pvp.games.GameInventory;
import camp.pvp.utils.PlayerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@Getter @Setter
public class CustomDuelKit {

    private final DuelKit duelKit;
    private final int slot;
    private ItemStack[] items;
    private String name;

    public CustomDuelKit(DuelKit duelKit, int slot) {
        this.duelKit = duelKit;
        this.slot = slot;
        this.items = duelKit.getGameInventory().getInventory();
        this.name = duelKit.getColor() + duelKit.getDisplayName() + " Kit " + slot;
    }

    public void apply(Player player) {
        PlayerInventory pi = player.getInventory();
        GameInventory gi = duelKit.getGameInventory();

        PlayerUtils.reset(player);

        pi.setArmorContents(gi.getArmor());
        pi.setContents(this.items);
    }
}
