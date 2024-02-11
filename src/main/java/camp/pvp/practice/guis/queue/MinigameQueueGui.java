package camp.pvp.practice.guis.queue;

import camp.pvp.practice.games.GameManager;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import org.bukkit.Material;

public class MinigameQueueGui extends ArrangedGui {

    public MinigameQueueGui(GameManager gameManager, GameProfile profile) {
        super("&6Minigame Queue");

        this.setAutoUpdate(true);
        this.setDefaultBackground();

        GuiButton skywars = new GuiButton(Material.EYE_OF_ENDER, "&6&lSkywars");
        skywars.setLore("&7&o4 Player Skywars", " ", "&aComing soon!");

        addButton(skywars);
    }
}
