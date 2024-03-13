package camp.pvp.practice.guis.games.duel;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.BaseKit;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.DuelRequest;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import org.bukkit.inventory.ItemStack;

public class DuelRequestKitSelectionGui extends ArrangedGui {
    public DuelRequestKitSelectionGui(Practice plugin, DuelRequest duelRequest) {
        super("&6Duel " + duelRequest.getOpponent().getName());

        for(GameKit gameKit : GameKit.values()) {
            BaseKit baseKit = gameKit.getBaseKit();
            if(!baseKit.getGameTypes().contains(GameQueue.GameType.DUEL)) continue;

            ItemStack item = baseKit.getIcon();
            GuiButton button = new GuiButton(item, "&6&l" + gameKit.getDisplayName());

            boolean hasPermission = duelRequest.getSender().getPlayer().hasPermission("practice.duel_requests.map_selection");

            button.setCloseOnClick(true);

            if(hasPermission) {
                button.setLore(
                        "&7Next, please select a map for",
                        "&7your duel against &6" + duelRequest.getOpponent().getName() + "&7."
                );
            } else {
                button.setLore(
                        "&7Click to duel &6" + duelRequest.getOpponent().getName() + "&7."
                );
            }

            button.setAction((pl, b, igui, click) -> {
                duelRequest.setKit(gameKit);
                if(hasPermission) {
                    new DuelRequestArenaSelectionGui(plugin, duelRequest, this).open(pl);
                } else {
                    duelRequest.send();
                }
            });

            getButtons().add(button);
        }
    }
}
