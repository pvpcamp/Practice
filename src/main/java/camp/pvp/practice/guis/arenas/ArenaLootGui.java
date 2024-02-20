package camp.pvp.practice.guis.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.LootChest;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArenaLootGui extends PaginatedGui {
    public ArenaLootGui(Arena arena) {
        super("&6Loot " + arena.getName(), 36);

        GuiButton back = new GuiButton(Material.ARROW, "&c&lBack to Arena Info");
        back.setAction((player, b, gui, click) -> {
            new ArenaInfoGui(Practice.getInstance().getArenaManager(), arena).open(player);
        });
        back.setSlot(4);
        addNavigationButton(back);

        List<LootChest> sortedList = new ArrayList<>(arena.getLootChests());
        sortedList.sort(Comparator.comparing(LootChest::getLootCategory));

        for(LootChest lootChest : sortedList) {
            GuiButton button = new GuiButton(Material.CHEST, "&6&lChest &7(" + lootChest.getLootCategory().toString() + "&7)");
            button.setLore(
                    "&6World: &f" + lootChest.getLocation().getWorld().getName(),
                    "&6X: &f" + lootChest.getLocation().getBlockX(),
                    "&6Y: &f" + lootChest.getLocation().getBlockY(),
                    "&6Z: &f" + lootChest.getLocation().getBlockZ(),
                    " ",
                    "&aLeft click to teleport.",
                    "&cRight click to delete.");
            button.setAction((player, button1, gui, clickType) -> {
                if(clickType.isRightClick()) {
                    arena.getLootChests().remove(lootChest);
                    player.sendMessage(ChatColor.GREEN + "You have removed this loot chest from arena " + arena.getName() + ".");
                    gui.removeButton(button1);
                    gui.updateGui();
                    return;
                }

                if(clickType.isLeftClick()) {
                    player.teleport(lootChest.getLocation());
                }
            });

            addButton(button);
        }
    }
}
