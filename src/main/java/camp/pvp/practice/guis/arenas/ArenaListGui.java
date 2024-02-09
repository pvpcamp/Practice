package camp.pvp.practice.guis.arenas;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaManager;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArenaListGui extends PaginatedGui {
    public ArenaListGui(ArenaManager arenaManager, Arena.Type type) {
        super("&6Arena List - " + (type == null ? "Any" : type.toString()), 36);

        GuiButton back = new GuiButton(Material.BEACON, "&cBack to Types");
        back.setSlot(4);
        back.setAction((player, button, gui, click) -> {
            new ArenaListChooseTypeGui().open(player);
        });
        addNavigationButton(back);

        List<Arena> arenas;
        if(type == null) {
            arenas = new ArrayList<>(arenaManager.getOriginalArenas());
        } else {
            arenas = arenaManager.getArenaForType(type);
        }

        arenas.sort(Comparator.comparing(Arena::getName));

        for(Arena arena : arenas) {
            GuiButton button = new GuiButton(arena.getType().getGuiMaterial(), "&6&l" + arena.getDisplayName() + " &a(" + arena.getName() + ")");

            List<String> lore = new ArrayList<>();
            lore.add("&6Type: &f" + arena.getType());
            lore.add("&6Queueable: &f" + (arena.isEnabled() ? "Yes" : "No"));

            if(!arenaManager.getArenaCopies(arena).isEmpty()) {
                lore.add("&6Copies: &f" + arenaManager.getArenaCopies(arena).size());
            }

            lore.add(" ");
            lore.add("&7Click to view more information.");

            button.setLore(lore);

            button.setAction((player, b, gui, click) -> {
                new ArenaInfoGui(arenaManager, arena).open(player);
            });

            addButton(button);
        }
    }
}
