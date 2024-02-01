package camp.pvp.practice.games;

import camp.pvp.practice.games.impl.teams.TeamGame;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class GameTeam {

    public enum Color {
        BLUE, RED;

        public ChatColor getChatColor() {
            switch(this) {
                case BLUE:
                    return ChatColor.BLUE;
                default:
                    return ChatColor.RED;
            }
        }

        public String getName() {
            switch(this) {
                case BLUE:
                    return "Blue";
                default:
                    return "Red";
            }
        }
    }

    private final Color color;
    private final TeamGame game;
    private boolean respawn, eliminated;
    private Location spawnLocation;

    public GameTeam(Color color, TeamGame game) {
        this.color = color;
        this.game = game;
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

    public boolean isEliminated() {
        if(eliminated) {
            return true;
        }

        return getAliveParticipants().isEmpty();
    }
}
