package camp.pvp.practice.games.sumo;

import camp.pvp.practice.Practice;
import camp.pvp.practice.profiles.GameProfile;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class EventParticipant {

    private final UUID uuid;
    private final String name;
    private int matches;
    private boolean alive, playing, active;

    public EventParticipant(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.alive = true;
        this.playing = true;
        this.active = true;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public GameProfile getProfile() {
        return Practice.getInstance().getGameProfileManager().getLoadedProfiles().get(uuid);
    }

    public void eliminate() {
        this.alive = false;
    }
}
