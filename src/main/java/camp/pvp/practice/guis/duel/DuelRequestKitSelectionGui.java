package camp.pvp.practice.guis.duel;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.DuelRequest;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class DuelRequestKitSelectionGui extends StandardGui {
    public DuelRequestKitSelectionGui(Practice plugin, DuelRequest duelRequest) {
        super("Duel " + duelRequest.getOpponent().getName(), 9);

        int x = 0;
        for(DuelKit duelKit : DuelKit.values()) {
            if (duelKit.isQueueable()) {
                ItemStack item = duelKit.getIcon();
                GuiButton button = new GuiButton(item, duelKit.getColor() + duelKit.getDisplayName());

                button.setCloseOnClick(true);
                button.setLore(
                        "&7Click to duel &6" + duelRequest.getOpponent().getName() + "&7!"
                );

                button.setAction((pl, igui) -> {
                    duelRequest.setKit(duelKit);
                    if(pl.hasPermission("practice.duel_requests.map_selection")) {
                        new DuelRequestArenaSelectionGui(plugin, duelRequest).open(pl);
                    } else {
                        duelRequest.send();
                    }
                });

                button.setSlot(x);
                this.addButton(button, false);
                x++;
            }
        }
    }
}
