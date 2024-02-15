package camp.pvp.practice.guis.queue;

import camp.pvp.practice.games.GameManager;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import org.bukkit.Material;

public class MinigameQueueGui extends ArrangedGui {

    public MinigameQueueGui(GameProfile profile) {
        super("&6Minigame Queue");

        this.setAutoUpdate(true);
        this.setDefaultBorder();

        GuiButton back = new GuiButton(Material.ARROW, "&c&lBack");
        back.setAction((p, b, g, click) -> new ChooseQueueGui(profile).open(p));
        back.setLore("&7Click to return to", "&7the play menu.");
        back.setSlot(0);
        back.setOverrideGuiArrangement(true);
        addButton(back);

        GuiButton skywars = new GuiButton(Material.EYE_OF_ENDER, "&6&lSkywars");
        skywars.setLore("&7&o4 Player Skywars", " ", "&aComing soon!");

        addButton(skywars);
    }
}
