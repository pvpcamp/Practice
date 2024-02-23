package camp.pvp.practice.guis.arenas;

import camp.pvp.core.utils.Colors;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaManager;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.Location;
import org.bukkit.Material;

public class ArenaRandomSpawnsGui extends PaginatedGui {
    public ArenaRandomSpawnsGui(ArenaManager arenaManager, Arena arena) {
        super("&6Random Spawns &7- &6" + arena.getDisplayName(), 36);

        GuiButton back = new GuiButton(Material.ARROW, "&c&lBack");
        back.setAction((player, guiButton, gui, clickType) -> {
            new ArenaInfoGui(arenaManager, arena).open(player);
        });
        back.setSlot(3);
        addNavigationButton(back);

        GuiButton add = new GuiButton(Material.EMERALD, "&a&lAdd Random Spawn");
        add.setAction((player, guiButton, gui, clickType) -> {
            Location location = player.getLocation();
            arena.getRandomSpawnLocations().add(location);
            player.sendMessage(Colors.get("&aAdded random spawn at &f" + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + "&a."));
            new ArenaRandomSpawnsGui(arenaManager, arena).open(player);
        });
        add.setSlot(5);
        addNavigationButton(add);

        for(Location location : arena.getRandomSpawnLocations()) {
            GuiButton button = new GuiButton(Material.GRASS, "&6&lRandom Spawn");
            button.setLore(
                    "&6X: &f" + location.getBlockX(),
                    "&6Y: &f" + location.getBlockY(),
                    "&6Z: &f" + location.getBlockZ(),
                    " ",
                    "&aLeft Click to Teleport.",
                    "&cRight Click to Remove.");
            button.setAction((player, guiButton, gui, clickType) -> {
                if(clickType.isLeftClick()) {
                    player.teleport(location);
                } else if(clickType.isRightClick()) {
                    arena.getRandomSpawnLocations().remove(location);
                    new ArenaRandomSpawnsGui(arenaManager, arena).open(player);
                }

            });

            addButton(button);
        }
    }
}
