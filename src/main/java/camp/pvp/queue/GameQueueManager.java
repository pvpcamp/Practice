package camp.pvp.queue;

import camp.pvp.Practice;
import camp.pvp.kits.DuelKit;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Getter @Setter
public class GameQueueManager {

    private Practice plugin;
    private Logger logger;
    private Set<GameQueue> gameQueues;
    public GameQueueManager(Practice plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.gameQueues = new HashSet<>();

        this.refreshQueues();
        logger.info("Started GameQueueManager.");
    }

    public void addQueue(GameQueue queue) {
        gameQueues.add(queue);
    }

    public void removeQueue(GameQueue queue) {
        gameQueues.remove(queue);
    }

    public void refreshQueues() {
        for(GameQueue q : gameQueues) {
            q.stopQueue();
        }

        for(DuelKit duelKit : DuelKit.values()) {
            if(duelKit.isQueueable()) {
                GameQueue queue = new GameQueue(plugin, duelKit, GameQueue.Type.UNRANKED);
                addQueue(queue);

                queue.startQueue();

                logger.info("Initialized UNRANKED queue for " + duelKit.name() + ".");

                if(duelKit.isRanked()) {
                    GameQueue rankedQueue = new GameQueue(plugin, duelKit, GameQueue.Type.RANKED);
                    addQueue(rankedQueue);

                    rankedQueue.startQueue();

                    logger.info("Initialized RANKED queue for " + duelKit.name() + ".");
                }
            }
        }

        logger.info("GameQueues have been refreshed.");
    }
}
