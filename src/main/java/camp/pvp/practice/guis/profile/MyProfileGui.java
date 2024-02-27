package camp.pvp.practice.guis.profile;

import camp.pvp.practice.guis.profile.cosmetics.CosmeticsGui;
import camp.pvp.practice.guis.statistics.StatisticsGui;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.StandardGui;
import org.bukkit.Material;

public class MyProfileGui extends StandardGui {

    public MyProfileGui(GameProfile profile) {
        super("&6My Profile", 27);

        setDefaultBorder();

        GuiButton cosmetics = new GuiButton(Material.ENDER_CHEST, "&6&lCosmetics");
        cosmetics.setSlot(11);
        cosmetics.setLore("&7Click to customize your cosmetics.");
        cosmetics.setAction((player, button, gui, click) -> {
            new CosmeticsGui(profile).open(player);
        });
        addButton(cosmetics);

        GuiButton statistics = new GuiButton(Material.DIAMOND_SWORD, "&6&lStatistics");
        statistics.setSlot(13);
        statistics.setCloseOnClick(true);
        statistics.setLore("&7Click to view your statistics.");
        statistics.setAction((player, button, gui, click) -> {
            new StatisticsGui(player, profile.getProfileElo(), profile.getProfileStatistics()).open(player);
        });
        addButton(statistics);

        GuiButton matches = new GuiButton(Material.BOOK_AND_QUILL, "&6&lMatch History");
        matches.setSlot(15);
        matches.setCloseOnClick(true);
        matches.setLore("&7Click to view your match history.");
        matches.setAction((player, button, gui, click) -> {
            player.performCommand("matches");
        });
        addButton(matches);
    }
}
