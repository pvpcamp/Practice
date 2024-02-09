package camp.pvp.practice.guis.games;

import camp.pvp.practice.games.Game;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.Gui;
import camp.pvp.utils.guis.GuiAction;
import camp.pvp.utils.guis.paginated.PaginatedGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SpectatorGui extends PaginatedGui {
    public SpectatorGui(Game game) {
        super("&6&lTeleportation Device", 27);

        for(Player player : game.getCurrentPlayersPlaying()) {
            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner(player.getName());
            skull.setItemMeta(skullMeta);

            GuiButton button = new GuiButton(skull, "&a" + player.getName());
            button.setCloseOnClick(true);
            button.setAction(new GuiAction() {
                @Override
                public void run(Player p, GuiButton button, Gui gui, ClickType click) {
                    p.teleport(player.getLocation());
                    p.sendMessage(ChatColor.GREEN + "You have teleported to " + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ".");
                }
            });

            this.addButton(button, false);
        }
    }
}
