package camp.pvp.practice.profiles.stats;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class ProfileIndividualStatistics {
    private int kills;
    private int deaths;
    private int wins;
    private int winStreak;
    private int bestWinStreak;

    public void incrementKills() {
        kills++;
    }

    public void incrementDeaths() {
        deaths++;
    }

    public void incrementWins() {
        wins++;
        winStreak++;
        if (winStreak > bestWinStreak) {
            bestWinStreak = winStreak;
        }
    }

    public void resetWinStreak() {
        winStreak = 0;
    }

    public void importFromMap(Map<String, Object> map) {
        kills = (int) map.get("kills");
        deaths = (int) map.get("deaths");
        wins = (int) map.get("wins");
        winStreak = (int) map.get("win_streak");
        bestWinStreak = (int) map.get("best_win_streak");
    }

    public Map<String, Object> export() {
        Map<String, Object> map = new HashMap<>();
        map.put("kills", kills);
        map.put("deaths", deaths);
        map.put("wins", wins);
        map.put("win_streak", winStreak);
        map.put("best_win_streak", bestWinStreak);
        return map;
    }
}

