package camp.pvp.games;

import camp.pvp.Practice;
import camp.pvp.games.impl.events.SumoEvent;
import camp.pvp.games.impl.events.TournamentEvent;

import java.util.*;

public class GameManager {

    private Practice plugin;
    public Map<UUID, Game> games;

    public GameManager(Practice plugin) {
        this.plugin = plugin;
        this.games = new HashMap<>();
    }

    public SumoEvent getActiveSumoEvent() {
        return null;
    }

    public TournamentEvent getActiveTournamentEvent() {
        return null;
    }

    public List<Game> getActiveGames() {
        List<Game> g = new ArrayList<>();
        for(Game game : games.values()) {
            Game.State state = game.getState();
            if(state != Game.State.INACTIVE) {
                g.add(game);
            }
        }

        return g;
    }

    public void addGame(Game game) {
        this.games.put(game.getUuid(), game);
    }
}
