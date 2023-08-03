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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuelRequestArenaSelectionGui extends PaginatedGui {
    public DuelRequestArenaSelectionGui(Practice plugin, DuelRequest duelRequest) {
        super("Select an Arena", 27);

        List<Arena> arenas = new ArrayList<>(plugin.getArenaManager().getArenas());
        Collections.sort(arenas);

        for(Arena arena : arenas) {
            if(duelRequest.getKit().getArenaTypes().contains(arena.getType()) && arena.isEnabled()) {
                GuiButton button = new GuiButton(Material.EMPTY_MAP, Colors.get("&6" + arena.getDisplayName()));

                button.setCloseOnClick(true);

                button.setLore(
                        "&7Click to duel &6" + duelRequest.getOpponent().getName() + "&7!"
                );

                button.setAction(new GuiAction() {
                    @Override
                    public void run(Player player, Gui gui) {
                        duelRequest.setArena(arena);
                        duelRequest.send();
                    }
                });

                this.addButton(button, false);
            }
        }
    }
}
