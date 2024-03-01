package camp.pvp.practice.guis.statistics;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.leaderboard.LeaderboardEntry;
import camp.pvp.utils.buttons.GuiButton;
import camp.pvp.utils.guis.ArrangedGui;

import java.util.*;

public class LeaderboardsGui extends ArrangedGui {
    public LeaderboardsGui() {
        super("&6ELO Leaderboards");

        Map<GameKit, List<LeaderboardEntry>> leaderboard = Practice.getInstance().getGameProfileManager().getLeaderboardUpdater().getLeaderboard();

        for(GameKit kit : GameKit.values()) {
            if(!kit.isDuelKit()) continue;
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
            addButton(button);
        }
    }
}
