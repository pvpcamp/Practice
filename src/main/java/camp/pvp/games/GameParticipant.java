package camp.pvp.games;

import camp.pvp.cooldowns.PlayerCooldown;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class GameParticipant {

    private final UUID uuid;
    private final String name;
    private boolean alive;

    private Map<PlayerCooldown.Type, PlayerCooldown> cooldowns;

    private UUID attacker;
    private EntityDamageEvent.DamageCause lastDamageCause;

    public int hits, currentCombo, longestCombo, thrownPotions, missedPotions;

    private PostGameInventory postGameInventory;

    public GameParticipant(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.cooldowns = new HashMap<>();
    }

    public void clearCooldowns() {
        for(PlayerCooldown cooldown : cooldowns.values()) {
            cooldown.remove();
        }
    }
}
