package camp.pvp.games;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Game {

    public enum State {
        INACTIVE, STARTING, ACTIVE, ENDING, ENDED;
    }

    public final UUID uuid;
    public Map<UUID, GameParticipant> participants;
    public State state;
    protected Game(UUID uuid) {
        this.uuid = uuid;
        this.participants = new HashMap<>();
        this.state = State.INACTIVE;
    }
}
