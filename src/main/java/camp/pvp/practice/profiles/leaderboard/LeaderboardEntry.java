package camp.pvp.practice.profiles.leaderboard;

import lombok.Data;

@Data
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {

    private final String name;
    private final int elo;

    @Override
    public int compareTo(LeaderboardEntry o) {
        return o.getElo() - this.getElo();
    }
}
