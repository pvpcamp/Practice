package camp.pvp.games;

import camp.pvp.games.impl.teams.TeamGame;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class GameTeam {

    public enum Color {
        BLUE, RED, YELLOW, WHITE;

        public ChatColor getChatColor() {
            switch(this) {
                case BLUE:
                    return ChatColor.BLUE;
                case RED:
                    return ChatColor.RED;
                case YELLOW:
                    return ChatColor.YELLOW;
                default:
                    return ChatColor.WHITE;
            }
        }
    }

    private final Color color;
    private final TeamGame game;
    private boolean eliminated;

    public GameTeam(Color color, TeamGame game, boolean eliminated) {
        this.color = color;
        this.game = game;
        this.eliminated = eliminated;
    }

    public Map<UUID, GameParticipant> getParticipants() {
        Map<UUID, GameParticipant> participants = new HashMap<>();
        for(Map.Entry<UUID, GameParticipant> entry : game.getParticipants().entrySet()) {
            if(entry.getValue().getTeam().equals(this)) {
                participants.put(entry.getKey(), entry.getValue());
            }
        }

        return participants;
    }

    public Map<UUID, GameParticipant> getAliveParticipants() {
        Map<UUID, GameParticipant> participants = new HashMap<>();
        for(Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
            if(entry.getValue().isAlive()) {
                participants.put(entry.getKey(), entry.getValue());
            }
        }

        return participants;
    }
}
