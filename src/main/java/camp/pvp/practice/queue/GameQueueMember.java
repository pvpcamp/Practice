package camp.pvp.practice.queue;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

@Getter @Setter
public class GameQueueMember {

    private final UUID uuid;
    private final String name;
    private final Date joined;
    private GameQueue queue;

    public GameQueueMember(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.joined = new Date();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
