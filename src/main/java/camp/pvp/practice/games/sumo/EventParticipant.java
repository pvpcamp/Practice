package camp.pvp.practice.games.sumo;

import camp.pvp.practice.Practice;
import camp.pvp.practice.profiles.GameProfile;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class EventParticipant {

    public enum PlayingState {
        ALIVE, SPECTATOR, DEAD
    }

    private final UUID uuid;
    private final String name;
    private final SumoEvent event;
    private PlayingState playingState;
    private boolean playerInEvent;
    private int matches;

    public EventParticipant(Player player, SumoEvent event) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.event = event;
        this.playingState = PlayingState.ALIVE;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public GameProfile getProfile() {
        return Practice.getInstance().getGameProfileManager().getLoadedProfiles().get(uuid);
    }

    public boolean isAlive() {
        return this.playingState.equals(PlayingState.ALIVE);
    }

    public void eliminate() {
        this.playingState = PlayingState.SPECTATOR;
    }

    public boolean isActive () {
        return this.playingState.equals(PlayingState.ALIVE) || this.playingState.equals(PlayingState.SPECTATOR);
    }

    public boolean isCurrentlyPlaying() {
        return event.getCurrentDuel() != null && event.getCurrentDuel().getParticipants().containsKey(this.getUuid());
    }

    public void incrementMatches() {
        this.matches++;
    }
}
