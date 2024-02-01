package camp.pvp.practice.guis.statistics;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.leaderboard.LeaderboardEntry;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;
import camp.pvp.utils.guis.StandardGui;

import java.util.*;

public class LeaderboardsGui extends ArrangedGui {
    public LeaderboardsGui() {
        super("&6ELO Leaderboards");

        setDefaultBorder();

        Map<DuelKit, List<LeaderboardEntry>> leaderboard = Practice.getInstance().getGameProfileManager().getLeaderboardUpdater().getLeaderboard();

        for(DuelKit kit : DuelKit.values()) {
            if(!kit.isQueueable()) continue;
            if(!kit.isRanked()) continue;

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
                        sb.append(" &a● #2: &r&a");
                        break;
                    case 2:
                        sb.append(" &e● #3: &r&e");
                        break;
                    default:
                        sb.append(" &7● #" + (i + 1) + ": ");
                        break;
                }

                sb.append(entry.getName() + " &7- &f" + entry.getElo());
                lines.add(sb.toString());
            }

            GuiButton button = new GuiButton(kit.getIcon(), "&6" + kit.getDisplayName());
            button.setLore(lines);
            getButtons().add(button);
        }
    }
}
