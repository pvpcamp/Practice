package camp.pvp.practice.games;

import camp.pvp.practice.games.impl.Duel;
import camp.pvp.practice.games.impl.events.GameEvent;
import camp.pvp.practice.games.tournaments.Tournament;
import camp.pvp.practice.Practice;
import camp.pvp.practice.queue.GameQueue;
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

    public GameEvent getActiveEvent() {
        for(Game game : games.values()) {
            Game.State state = game.getState();
            if(game instanceof GameEvent && !state.equals(Game.State.ENDED)) {
                return (GameEvent) game;
            }
        }

        return null;
    }

    public List<Game> getActiveGames() {
        List<Game> g = new ArrayList<>();
        for(Game game : games.values()) {
            Game.State state = game.getState();
            if(!Arrays.asList(Game.State.INACTIVE, Game.State.ENDED).contains(state)) {
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

    public int getTotalInGame(GameQueue.Type queueType) {
        int i = 0;
        for(Game game : getActiveGames()) {
            if(game instanceof Duel) {
                Duel duel = (Duel) game;
                if(duel.getQueueType().equals(queueType)) {
                    i += game.getAlive().size();
                }
            }
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
