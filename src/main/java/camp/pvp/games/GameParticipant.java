package camp.pvp.games;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

@Getter @Setter
public class GameParticipant {

    public final UUID uuid;
    public final String name;
    public boolean alive;

    public UUID attacker;
    public EntityDamageEvent.DamageCause lastDamageCause;

    public int hits, currentCombo, longestCombo;


    public GameParticipant(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }
}
