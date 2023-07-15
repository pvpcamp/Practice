package camp.pvp.games.tournaments;

import camp.pvp.arenas.Arena;
import camp.pvp.games.Game;
import camp.pvp.games.GameParticipant;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class TournamentMatch {

    private Map<UUID, TournamentParticipant> participants;
    private Game game;

    public TournamentMatch(Game game, TournamentParticipant... participants) {
        this.game = game;
        this.participants = new HashMap<>();

        for(TournamentParticipant p : participants) {
            this.getParticipants().put(p.getUuid(), p);
        }
    }

    public void startGame() {
        for(TournamentParticipant p : participants.values()) {
            game.join(p.getPlayer());

            if(p.getTeam() != null) {
                // TODO: 2v2 Tournaments
            }
        }

        game.start();
    }
}
