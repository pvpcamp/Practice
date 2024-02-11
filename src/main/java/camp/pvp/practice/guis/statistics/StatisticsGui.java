package camp.pvp.practice.guis.statistics;

import camp.pvp.practice.Practice;
import camp.pvp.practice.guis.profile.MyProfileGui;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.stats.ProfileELO;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class StatisticsGui extends ArrangedGui {
    public StatisticsGui(Player opener, ProfileELO profileELO) {
        super("&6" + profileELO.getName() + " Statistics");

        setDefaultNavigationBar();

        if(opener.getUniqueId().equals(profileELO.getUuid())) {
            GuiButton myProfile = new GuiButton(Material.SKULL_ITEM, "&6&lGo to My Profile");
            myProfile.setDurability((short) 3);
            SkullMeta meta = (SkullMeta) myProfile.getItemMeta();
            meta.setOwner(opener.getName());
            myProfile.setItemMeta(meta);

            myProfile.setAction((p, b, g, click) -> {
                new MyProfileGui(Practice.getInstance().getGameProfileManager().getLoadedProfiles().get(opener.getUniqueId())).open(p);
            });

            myProfile.setSlot(4);
            myProfile.setOverrideGuiArrangement(true);
            addButton(myProfile);
        }

        for(DuelKit kit : DuelKit.values()) {
            if(!kit.isQueueable()) continue;
            if(!kit.isRanked()) continue;

            List<String> lines = new ArrayList<>();
            lines.add("&6ELO: &f" + profileELO.getRatings().get(kit));

            GuiButton button = new GuiButton(kit.getIcon(), "&6&l" + kit.getDisplayName());
            button.setLore(lines);
            addButton(button);
        }
    }
}
