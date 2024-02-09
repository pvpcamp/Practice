package camp.pvp.practice.guis.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import org.bukkit.Material;

public class ArenaListChooseTypeGui extends ArrangedGui {
    public ArenaListChooseTypeGui() {
        super("&6Arena List - Choose Type");

        GuiButton any = new GuiButton(Material.BEACON, "&6&lAll Arenas");
        any.setAction((player, b, gui, click) -> {
            new ArenaListGui(Practice.getInstance().getArenaManager(), null).open(player);
        });
        any.setLore("&7Click to view " + Practice.getInstance().getArenaManager().getOriginalArenas().size() + " arenas.");
        addButton(any);

        for(Arena.Type type : Arena.Type.values()) {
            GuiButton button = new GuiButton(type.getGuiMaterial(), "&6&l" + type);
            button.setLore("&7Click to view " + Practice.getInstance().getArenaManager().getArenaForTypeAny(type).size() + " &7arenas.");
            button.setAction((player, b, gui, click) -> {
                new ArenaListGui(Practice.getInstance().getArenaManager(), type).open(player);
            });
            addButton(button);
        }
    }
}
