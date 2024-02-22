package camp.pvp.practice.profiles.stats;

import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.impl.Duel;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.utils.TimeUtil;
import lombok.Data;
import org.bson.Document;

import java.util.*;

@Data
public class MatchRecord {

    private final UUID uuid;
    private UUID winner, loser;
    private Date started, ended;
    private String winnerName, loserName;

    private int eloChange, beforeWinnerElo, befloreLoserElo;
    private boolean rolledBack;

    private GameQueue.Type queueType;
    private GameKit kit;

    public MatchRecord(UUID uuid) {
        this.uuid = uuid;
    }

    public MatchRecord(Duel duel, GameParticipant winner, GameParticipant loser, int eloChange, int beforeWinnerElo, int befloreLoserElo) {
        this.uuid = UUID.randomUUID();
        this.winner = winner.getUuid();
        this.loser = loser.getUuid();
        this.winnerName = winner.getName();
        this.loserName = loser.getName();
        this.ended = duel.getEnded();
        this.started = duel.getStarted();
        this.eloChange = eloChange;
        this.beforeWinnerElo = beforeWinnerElo;
        this.befloreLoserElo = befloreLoserElo;
        this.rolledBack = false;
        this.queueType = duel.getQueueType();
        this.kit = duel.getKit();
    }

    public void importFromDocument(Document doc) {
        this.winner = doc.get("winner", UUID.class);
        this.loser = doc.get("loser", UUID.class);
        this.winnerName = doc.getString("winner_name");
        this.loserName = doc.getString("loser_name");
        this.started = doc.getDate("started");
        this.ended = doc.getDate("ended");
        this.eloChange = doc.getInteger("elo_change");
        this.beforeWinnerElo = doc.getInteger("before_winner_elo");
        this.befloreLoserElo = doc.getInteger("before_loser_elo");
        this.rolledBack = doc.getBoolean("rolled_back", false);
        this.queueType = GameQueue.Type.valueOf(doc.get("queue_type", "UNRANKED"));
        this.kit = GameKit.valueOf(doc.get("kit", "NO_DEBUFF"));
    }

    public Map<String, Object> export() {
        Map<String, Object> export = new HashMap<>();
        export.put("winner", winner);
        export.put("loser", loser);
        export.put("winner_name", winnerName);
        export.put("loser_name", loserName);
        export.put("started", started);
        export.put("ended", ended);
        export.put("rolled_back", rolledBack);
        export.put("kit", kit.name());
        export.put("queue_type", queueType.name());
        export.put("elo_change", eloChange);
        export.put("before_winner_elo", beforeWinnerElo);
        export.put("before_loser_elo", befloreLoserElo);

        return export;
    }

    public String getMatchDuration() {
        return TimeUtil.get(getEnded(), getStarted());
    }

    public String getOpponentName(UUID uuid) {
        return uuid.equals(winner) ? loserName : winnerName;
    }

    public int getWinnerElo() {
        return beforeWinnerElo + eloChange;
    }

    public int getLoserElo() {
        return befloreLoserElo - eloChange;
    }
}
