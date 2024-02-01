package camp.pvp.practice.guis.statistics;

import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.stats.ProfileELO;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;

import java.util.*;

public class StatisticsGui extends ArrangedGui {
    public StatisticsGui(ProfileELO profileELO) {
        super("&6" + profileELO.getName() + " Statistics");

        setDefaultBorder();

        for(DuelKit kit : DuelKit.values()) {
            if(!kit.isQueueable()) continue;
            if(!kit.isRanked()) continue;

            List<String> lines = new ArrayList<>();
            lines.add("&6ELO: &f" + profileELO.getRatings().get(kit));

            GuiButton button = new GuiButton(kit.getIcon(), "&6&l" + kit.getDisplayName());
            button.setLore(lines);
            getButtons().add(button);
        }
    }
}
