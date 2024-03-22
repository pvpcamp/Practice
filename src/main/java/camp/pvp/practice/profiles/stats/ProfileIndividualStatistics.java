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
    private int elo = 1000;

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
        kills = (int) map.getOrDefault("kills", 0);
        deaths = (int) map.getOrDefault("deaths", 0);
        wins = (int) map.getOrDefault("wins", 0);
        winStreak = (int) map.getOrDefault("win_streak", 0);
        bestWinStreak = (int) map.getOrDefault("best_win_streak", 0);
        elo = (int) map.getOrDefault("elo", 1000);
    }

    public Map<String, Object> export() {
        Map<String, Object> map = new HashMap<>();
        map.put("kills", kills);
        map.put("deaths", deaths);
        map.put("wins", wins);
        map.put("win_streak", winStreak);
        map.put("best_win_streak", bestWinStreak);
        map.put("elo", elo);
        return map;
    }
}

