package camp.pvp.arenas;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class Arena {

    public enum Type {
        BUILD, DUEL, DUEL_BUILD, FFA, EVENT;
    }

    private @Getter @Setter String name, displayName;
    private @Getter @Setter Arena.Type type;
    private @Getter @Setter Set<ArenaPosition> positions;
    private @Getter @Setter boolean enabled, inUse;
}
