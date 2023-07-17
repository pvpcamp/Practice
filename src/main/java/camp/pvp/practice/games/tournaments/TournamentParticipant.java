package camp.pvp.practice.games.tournaments;

import camp.pvp.practice.games.Game;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
public class TournamentParticipant {

    private final UUID uuid;
    private final String name;
    private UUID team;
    private boolean eliminated;
    private Game game;

    public TournamentParticipant(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
