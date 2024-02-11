package camp.pvp.practice.guis.games.duel;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.profiles.DuelRequest;
import camp.pvp.practice.utils.Colors;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuelRequestArenaSelectionGui extends PaginatedGui {
    public DuelRequestArenaSelectionGui(Practice plugin, DuelRequest duelRequest, DuelRequestKitSelectionGui kitSelectionGui) {
        super("Select an Arena", 27);

        List<Arena> arenas = new ArrayList<>(plugin.getArenaManager().getOriginalArenas());
        Collections.sort(arenas);

        GuiButton kitSelection = new GuiButton(Material.BOOK, "&6Back to Kit Selection");
        kitSelection.setSlot(4);
        kitSelection.setAction(new GuiAction() {
            @Override
            public void run(Player player, GuiButton guiButton, Gui gui, ClickType clickType) {
                kitSelectionGui.open(player);
            }
        });
        addNavigationButton(kitSelection);

        for(Arena arena : arenas) {
            if(duelRequest.getKit().getArenaTypes().contains(arena.getType()) && arena.isEnabled()) {
                GuiButton button = new GuiButton(Material.EMPTY_MAP, Colors.get("&6" + arena.getDisplayName()));

                button.setCloseOnClick(true);

                button.setLore(
                        "&7Click to duel &6" + duelRequest.getOpponent().getName() + "&7!"
                );

                button.setAction(new GuiAction() {
                    @Override
                    public void run(Player player, GuiButton guiButton, Gui gui, ClickType clickType) {
                        duelRequest.setArena(arena);
                        duelRequest.send();
                    }
                });

                this.addButton(button, false);
            }
        }
    }
}
