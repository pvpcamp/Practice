package camp.pvp.practice.guis.statistics;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.tasks.LeaderboardEntry;
import camp.pvp.practice.profiles.tasks.LeaderboardUpdater;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.StandardGui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderboardsGui extends StandardGui {
    public LeaderboardsGui() {
        super("&6&lELO Leaderboards", 27);

        this.setDefaultBackground();

        Map<DuelKit, List<LeaderboardEntry>> leaderboard = Practice.instance.getGameProfileManager().getLeaderboardUpdater().getLeaderboard();

        for(DuelKit kit : DuelKit.values()) {
            if(kit.isQueueable() && kit.isRanked()) {
                List<LeaderboardEntry> entries = leaderboard.get(kit);
                List<String> lines = new ArrayList<>();

                for(int i = 0; i < entries.size(); i++) {
                    StringBuilder sb = new StringBuilder();
                    LeaderboardEntry entry = entries.get(i);
                    switch(i) {
                        case 0:
                            sb.append(" &6&l● #1: &r&6");
                            break;
                        case 1:
                            sb.append(" &a&l● #2: &r&a");
                            break;
                        case 2:
                            sb.append(" &e&l● #3: &r&e");
                            break;
                        default:
                            sb.append(" &e● #" + (i + 1) + ": ");
                            break;
                    }

                    sb.append(entry.getName() + " &7- &f" + entry.getElo());
                    lines.add(sb.toString());
                }

                GuiButton button = new GuiButton(kit.getIcon(), kit.getColor() + kit.getDisplayName());
                button.setSlot(kit.getRankedSlot());
                button.setLore(lines);
                this.addButton(button, false);
            }
        }
    }
}
