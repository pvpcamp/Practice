package camp.pvp.games;

import camp.pvp.Practice;
import camp.pvp.games.impl.events.SumoEvent;
import camp.pvp.games.impl.events.TournamentEvent;

import java.util.*;

public class GameManager {

    private Practice plugin;
    public Map<UUID, Game> games;
    public Map<UUID, PostGameInventory> postGameInventories;

    public GameManager(Practice plugin) {
        this.plugin = plugin;
        this.games = new HashMap<>();
        this.postGameInventories = new HashMap<>();
    }

    public SumoEvent getActiveSumoEvent() {
        for(Game game : games.values()) {
            Game.State state = game.getState();
            if(game instanceof SumoEvent && !state.equals(Game.State.INACTIVE) || !state.equals(Game.State.ENDED)) {
                return (SumoEvent) game;
            }
        }

        return null;
    }

    public TournamentEvent getActiveTournamentEvent() {
        for(Game game : games.values()) {
            Game.State state = game.getState();
            if(game instanceof TournamentEvent && !state.equals(Game.State.INACTIVE) || !state.equals(Game.State.ENDED)) {
                return (TournamentEvent) game;
            }
        }

        return null;
    }

    public List<Game> getActiveGames() {
        List<Game> g = new ArrayList<>();
        for(Game game : games.values()) {
            Game.State state = game.getState();
            if(state.equals(Game.State.INACTIVE) || !state.equals(Game.State.ENDED)) {
                g.add(game);
            }
        }

        return g;
    }

    public void addGame(Game game) {
        this.games.put(game.getUuid(), game);
    }

    public void addInventory(PostGameInventory postGameInventory) {
        this.postGameInventories.put(postGameInventory.getUuid(), postGameInventory);
    }

    public void shutdown() {
        for(Game game : getActiveGames()) {
            game.forceEnd();
        }
    }
}
