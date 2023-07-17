package camp.pvp.practice.games;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
public class GameSpectator {

    public final UUID uuid;
    public final String name;

    public GameSpectator(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
}
