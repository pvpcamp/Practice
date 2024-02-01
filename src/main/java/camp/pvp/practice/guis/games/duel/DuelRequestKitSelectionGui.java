package camp.pvp.practice.guis.games.duel;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.DuelRequest;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DuelRequestKitSelectionGui extends ArrangedGui {
    public DuelRequestKitSelectionGui(Practice plugin, DuelRequest duelRequest) {
        super("&6Duel " + duelRequest.getOpponent().getName());

        for(DuelKit duelKit : DuelKit.values()) {
            if(!duelKit.isQueueable()) continue;

            ItemStack item = duelKit.getIcon();
            GuiButton button = new GuiButton(item, "&6&l" + duelKit.getDisplayName());

            boolean hasPermission = duelRequest.getSender().getPlayer().hasPermission("practice.duel_requests.map_selection");

            button.setCloseOnClick(true);

            if(hasPermission) {
                button.setLore(
                        "&7Next, please select a map for" +
                        "&7your duel against &6" + duelRequest.getOpponent().getName() + "&7."
                );
            } else {
                button.setLore(
                        "&7Click to duel &6" + duelRequest.getOpponent().getName() + "&7."
                );
            }

            button.setAction((pl, igui) -> {
                duelRequest.setKit(duelKit);
                if(hasPermission) {
                    new DuelRequestArenaSelectionGui(plugin, duelRequest).open(pl);
                } else {
                    duelRequest.send();
                }
            });

            getButtons().add(button);
        }
    }
}
