package camp.pvp.practice.games.minigames.tag;

import camp.pvp.practice.games.GameParticipant;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class TNTTagParticipant extends GameParticipant {

    @Getter @Setter private boolean tagged;

    public TNTTagParticipant(UUID uuid, String name) {
        super(uuid, name);
    }
}
