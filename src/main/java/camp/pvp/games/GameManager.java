package camp.pvp.games;

import camp.pvp.Practice;
import camp.pvp.games.impl.events.SumoEvent;
import camp.pvp.games.tournaments.Tournament;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.logging.Logger;

@Getter @Setter
public class GameManager {

    private Practice plugin;
    private Logger logger;
    private Map<UUID, Game> games;
    private Tournament tournament;

    private Map<UUID, PostGameInventory> postGameInventories;

    public GameManager(Practice plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.games = new HashMap<>();
        this.postGameInventories = new HashMap<>();

        this.logger.info("Started GameManager.");
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

    public int getTotalInGame() {
        int i = 0;
        for(Game game : getActiveGames()) {
            i += game.countAll();
        }

        return i;
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
