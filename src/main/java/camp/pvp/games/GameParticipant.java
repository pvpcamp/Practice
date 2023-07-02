package camp.pvp.games;

import java.util.UUID;

public class GameParticipant {

    private final UUID uuid;
    private String name;

    public GameParticipant(UUID uuid) {
        this.uuid = uuid;
    }
}
