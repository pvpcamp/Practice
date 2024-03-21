package camp.pvp.practice.guis.arenas;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import org.bukkit.Location;
import org.bukkit.Material;

public class ArenaPositionsGui extends ArrangedGui {

    public ArenaPositionsGui(Arena arena) {
        super("&6Positions - " + arena.getDisplayName());

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

            button.setCloseOnClick(true);

            if(validPosition) {
                button.setLore(
                        "&6World: &f" + arenaPosition.getLocation().getWorld().getName(),
                        "&6X: &f" + arenaPosition.getLocation().getX(),
                        "&6Y: &f" + arenaPosition.getLocation().getY(),
                        "&6Z: &f" + arenaPosition.getLocation().getZ(),
                        "&6Yaw: &f" + arenaPosition.getLocation().getYaw(),
                        "&6Pitch: &f" + arenaPosition.getLocation().getPitch(),
                        " ",
                        "&aLeft click to set to your current player position.",
                        "&cRight click to set to your selected position. (Golden Axe)");
            } else {
                button.setLore(
                        "&cThis position is not set!",
                        " ",
                        "&aLeft click to set to your current player position.",
                        "&cRight click to set to your selected position. (Golden Axe)");
            }

            button.setAction((player, b, gui, click) -> {
                GameProfile profile = Practice.getInstance().getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
                Location location = profile.getSelectedLocation();
                if(click.isRightClick()) {
                    if(location == null) {
                        player.sendMessage(Colors.get("&cYou must select a position first with the Golden Axe."));
                        return;
                    }
                } else {
                    location = player.getLocation();
                }

                if(arena.getWorldId() == null) {
                    arena.setWorldId(location.getWorld().getUID());
                }

                arena.getPositions().put(position, new ArenaPosition(position, location));
                player.sendMessage(Colors.get("&aPosition &f" + position + "&a for arena &f" + arena.getName() + "&a set to &f" + location.getX() + " " + location.getY() + " " + location.getZ() + "&a."));

                Practice.getInstance().getArenaManager().updateArenaCopies(arena, false);
            });

            addButton(button);
        }
    }
}
