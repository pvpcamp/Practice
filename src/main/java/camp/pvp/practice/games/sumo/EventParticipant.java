package camp.pvp.practice.games.sumo;

import lombok.Data;

import java.util.UUID;

@Data
public class EventParticipant {

    private final UUID uuid;
    private final String name;
    private int kills;
    private boolean alive;

    public EventParticipant(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
}
