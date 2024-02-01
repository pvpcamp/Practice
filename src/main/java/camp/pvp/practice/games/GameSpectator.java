package camp.pvp.practice.games;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
public class GameSpectator {

    private final UUID uuid;
    private final String name;
    private boolean visibleToPlayers;


    public GameSpectator(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        visibleToPlayers = false;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
