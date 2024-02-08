package camp.pvp.practice.guis.profile.cosmetics;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;

public class CosmeticsGui extends StandardGui {
    public CosmeticsGui(GameProfile profile) {
        super("&6Cosmetics", 27);

        setDefaultBorder();

        GuiButton comingSoon = new GuiButton(Material.PAPER, "&6&lComing Soon!");
        comingSoon.setLore(
                "&7We are in the process of",
                "&7creating cosmetics for you to",
                "&7use in lobbies and in game."
        );
        comingSoon.setSlot(13);
        addButton(comingSoon);
    }
}
