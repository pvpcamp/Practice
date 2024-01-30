package camp.pvp.practice.games.tournaments;

import camp.pvp.practice.games.Game;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.HashMap;
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
        boolean startGame = true;
        for(TournamentParticipant p : participants.values()) {

            if(p.isEliminated())  {
                startGame = false;
                break;
            }

            game.join(p.getPlayer());

            if(p.getTeam() != null) {
                // TODO: 2v2 Tournaments
            }
        }

        if(startGame) {
            game.start();
        } else {
            game.setState(Game.State.ENDED);
            for(TournamentParticipant p : participants.values()) {
                if(!p.isEliminated()) {
                    p.getPlayer().sendMessage(ChatColor.GREEN + "Your opponent left the tournament, so you will not have a match this round.");
                }
            }
        }
    }
}
