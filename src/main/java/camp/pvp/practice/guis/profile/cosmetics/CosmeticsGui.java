package camp.pvp.practice.guis.profile.cosmetics;

import camp.pvp.practice.Practice;
import camp.pvp.practice.guis.profile.MyProfileGui;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;

public class CosmeticsGui extends StandardGui {
    public CosmeticsGui(GameProfile profile) {
        super("&6Cosmetics", 27);

        setDefaultBorder();

        GuiButton myProfile = new GuiButton(Material.NETHER_STAR, "&6&lGo to My Profile");
        myProfile.setAction((p, b, g, click) -> {
            new MyProfileGui(profile).open(p);
        });
        myProfile.setSlot(4);
        addButton(myProfile);

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
