package camp.pvp.practice.guis.statistics;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.stats.ProfileELO;
import camp.pvp.practice.profiles.tasks.LeaderboardEntry;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.StandardGui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsGui extends StandardGui {
    public StatisticsGui(ProfileELO profileELO) {
        super("&6&l" + profileELO.getName() + " Statistics", 27);

        this.setDefaultBackground();

        for(DuelKit kit : DuelKit.values()) {
            if(kit.isQueueable() && kit.isRanked()) {
                List<String> lines = new ArrayList<>();
                lines.add("&6ELO: &f" + profileELO.getRatings().get(kit));

                GuiButton button = new GuiButton(kit.getIcon(), kit.getColor() + kit.getDisplayName());
                button.setSlot(kit.getRankedSlot());
                button.setLore(lines);
                this.addButton(button, false);
            }
        }
    }
}
