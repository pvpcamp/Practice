package camp.pvp.queue;

import camp.pvp.Practice;
import camp.pvp.kits.DuelKit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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

    public GameQueueMember addToQueue(Player player, GameQueue gameQueue) {
        if(getQueue(player) == null) {
            GameQueueMember gqm = new GameQueueMember(player.getUniqueId(), player.getName());
            gqm.setQueue(gameQueue);
            gameQueue.getQueueMembers().add(gqm);

            return gqm;
        }

        return null;
    }

    public boolean removeFromQueue(Player player) {
        return removeFromQueue(player.getUniqueId());
    }

    public boolean removeFromQueue(UUID uuid) {
        GameQueue queue = getQueue(uuid);
        if(queue != null) {
            return findQueueMember(queue, uuid) != null;
        }
        return false;
    }

    public GameQueueMember findQueueMember(GameQueue queue, UUID uuid) {
        for(GameQueueMember qm : queue.getQueueMembers()) {
            if(qm.getUuid().equals(uuid)) {
                return qm;
            }
        }

        return null;
    }

    public GameQueue getQueue(Player player) {
        return getQueue(player.getUniqueId());
    }

    public GameQueue getQueue(UUID uuid) {
        for(GameQueue q : getGameQueues()) {
            GameQueueMember qm = findQueueMember(q, uuid);
            if(qm != null) {
                return q;
            }
        }

        return null;
    }

    public int getTotalInQueue() {
        int i = 0;
        for(GameQueue q : gameQueues) {
            i += q.getQueueMembers().size();
        }

        return i;
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
