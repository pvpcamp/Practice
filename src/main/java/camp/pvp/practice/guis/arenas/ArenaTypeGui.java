package camp.pvp.practice.guis.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaManager;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;

public class ArenaTypeGui extends ArrangedGui {

    public ArenaTypeGui(ArenaManager arenaManager, Arena arena) {
        super("&6Arena Type - " + arena.getDisplayName());

        for(Arena.Type type : Arena.Type.values()) {
            GuiButton button = new GuiButton(type.getGuiMaterial(), "&6&l" + type);
            button.setButtonUpdater((player, gui) -> {
                if(arena.getType() == type) {
                    button.setLore("&7&oThis arena type is already selected.");
                    button.updateName("&6&l" + type + " &7(Selected)");
                } else {
                    button.setLore(
                            "&7Click to change the type of",
                            "&7this arena to &f" + type + "&7.");
                    button.updateName("&6&l" + type);
                }
            });

            button.setAction((player, b, gui, click) -> {
                arena.setType(type);
                new ArenaInfoGui(arenaManager, arena).open(player);
            });

            addButton(button);
        }
    }
}
