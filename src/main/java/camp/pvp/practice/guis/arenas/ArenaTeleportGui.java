package camp.pvp.practice.guis.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import org.bukkit.Material;

public class ArenaTeleportGui extends ArrangedGui {

    public ArenaTeleportGui(Arena arena) {
        super("&6Teleport - " + arena.getDisplayName());

        GuiButton back = new GuiButton(Material.ARROW, "&c&lBack to Arena Info");
        back.setAction((player, b, gui, click) -> {
            new ArenaInfoGui(Practice.getInstance().getArenaManager(), arena).open(player);
        });
        back.setOverrideGuiArrangement(true);
        back.setSlot(0);
        addButton(back);


        for(String position : arena.getType().getValidPositions()) {
            ArenaPosition arenaPosition = arena.getPositions().get(position);
            boolean validPosition = arenaPosition != null;

            GuiButton button = new GuiButton(validPosition ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, (validPosition ? "&6&l" : "&c&l") + position);

            if(validPosition) {
                button.setLore(
                        "&6World: &f" + arenaPosition.getLocation().getWorld().getName(),
                        "&6X: &f" + arenaPosition.getLocation().getX(),
                        "&6Y: &f" + arenaPosition.getLocation().getY(),
                        "&6Z: &f" + arenaPosition.getLocation().getZ(),
                        " ",
                        "&7Click to teleport to this position.");
                button.setAction((player, b, gui, click) -> {
                    player.teleport(arenaPosition.getLocation());
                });
                button.setCloseOnClick(true);
            } else {
                button.setLore(
                        "&cThis position is not set!",
                        "&cPlease use /arena positions " + arena.getName(),
                        "&cto set positions for this arena.");
            }

            addButton(button);
        }
    }
}
