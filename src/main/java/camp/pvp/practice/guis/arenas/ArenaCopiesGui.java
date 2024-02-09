package camp.pvp.practice.guis.arenas;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaManager;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArenaCopiesGui extends PaginatedGui {
    public ArenaCopiesGui(ArenaManager arenaManager, Arena arena) {
        super("&6" + arena.getDisplayName() + " Copies", 27);

        GuiButton back = new GuiButton(Material.BEACON, "&cBack to Arena Info");
        back.setSlot(4);
        back.setAction((player, button, gui, click) -> {
            new ArenaInfoGui(arenaManager, arena).open(player);
        });
        addNavigationButton(back);

        List<Arena> arenas = new ArrayList<>(arenaManager.getArenaCopies(arena));
        arenas.sort(Comparator.comparing(Arena::getName));

        for(Arena copy : arenas) {
            GuiButton button = new GuiButton(copy.getType().getGuiMaterial(), "&6&l" + copy.getDisplayName() + " &a(" + copy.getName() + ")");
            button.setAction((player, b, gui, click) -> {
                new ArenaInfoGui(arenaManager, copy).open(player);
            });
            addButton(button);
        }
    }
}
