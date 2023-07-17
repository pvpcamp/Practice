package camp.pvp.practice.arenas;

import lombok.Getter;
import org.bukkit.Location;

public class ArenaPosition {

    private @Getter final String position;
    private @Getter final Location location;

    public ArenaPosition(String position, Location location) {
        this.position = position;
        this.location = location;
    }
}
