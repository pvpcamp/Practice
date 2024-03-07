package camp.pvp.practice.games;

import camp.pvp.practice.games.impl.Duel;
import camp.pvp.practice.games.impl.teams.tasks.HCFEffectUpdater;
import camp.pvp.practice.games.minigames.Minigame;
import camp.pvp.practice.games.sumo.SumoEvent;
import camp.pvp.practice.games.tournaments.Tournament;
import camp.pvp.practice.Practice;
import camp.pvp.practice.queue.GameQueue;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Logger;

@Getter @Setter
public class GameManager {

    private Practice plugin;
    private Logger logger;
    private Map<UUID, Game> games;
    private Tournament tournament;
    private SumoEvent sumoEvent;
    private BukkitTask borderTask;

    private Map<UUID, PostGameInventory> postGameInventories;

    public GameManager(Practice plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.games = new HashMap<>();
        this.postGameInventories = new HashMap<>();

        this.borderTask = Bukkit.getScheduler().runTaskTimer(plugin, ()-> {
            for(Game game : getActiveGames()) {
                for(Entity entity : game.getEntities()) {
                    if(!game.isInBorder(entity.getLocation()) && entity.getTicksLived() > 100) {
                        entity.remove();
                    }
                }
            }
        }, 0, 20);

        Bukkit.getScheduler().runTaskTimer(plugin, new HCFEffectUpdater(this), 0, 2);

        this.logger.info("Initialized GameManager.");
    }

    public SumoEvent getActiveEvent() {

        if(sumoEvent != null && !sumoEvent.getState().equals(SumoEvent.State.ENDED)) {
            return sumoEvent;
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

    public boolean isEventRunning() {
        return getActiveEvent() != null || getTournament() != null;
    }

    public int getTotalInGame() {
        int i = 0;
        for(Game game : getActiveGames()) {
            i += game.countAll();
        }

        return i;
    }

    public int getTotalInGame(GameQueue.GameType gameType, GameQueue.Type queueType) {
        int i = 0;
        for(Game game : getActiveGames()) {
            if(game instanceof Duel && gameType.equals(GameQueue.GameType.DUEL)) {
                Duel duel = (Duel) game;
                if(duel.getQueueType() != null && duel.getQueueType().equals(queueType)) {
                    i += game.getCurrentPlaying().size();
                }
            }

            if(game instanceof Minigame && gameType.equals(GameQueue.GameType.MINIGAME)) {
                i += game.getCurrentPlaying().size();
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
            game.forceEnd(false);
        }
    }
}
