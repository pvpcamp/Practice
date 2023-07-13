package camp.pvp.parties;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
public class PartyMember {

    private final UUID uuid;
    private final String name;
    private boolean leader;

    public PartyMember(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
