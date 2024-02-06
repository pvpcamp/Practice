package camp.pvp.practice.games.sumo;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

import java.util.*;

public class SumoEvent {

    private Map<UUID, EventParticipant> participants;
    private List<SumoEventDuel> games;
    private Arena arena;
    private State state;

    public SumoEvent() {
        this.participants = new HashMap<>();
        this.games = new ArrayList<>();
        this.state = State.INACTIVE;
    }

    public void eliminate(Player player, boolean leftGame) {

    }

    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();

        switch(state) {
            case STARTING -> {
                lines.add("hi");
            }
            case IN_GAME -> {
                if(getCurrentDuel() != null) lines.addAll(getCurrentDuel().getScoreboard(profile));
            }
            case ENDING -> {

            }
        }

        return lines;
    }

    private EventParticipant getWinner() {
        return null;
    }

    public SumoEventDuel getCurrentDuel() {
        for(SumoEventDuel duel : games) {
            if(!duel.getState().equals(Game.State.ENDED)) {
                return duel;
            }
        }

        return null;
    }

    public enum State {
        INACTIVE, STARTING, IN_GAME, ENDING, ENDED;

        @Override
        public String toString() {
            switch(this) {
                case INACTIVE:
                    return "Inactive";
                case STARTING:
                    return "Starting";
                case IN_GAME:
                    return "In Game";
                default:
                    return "Ended";
            }
        }
    }
}
