package camp.pvp.practice.queue;

import camp.pvp.practice.games.minigames.QueueableMinigame;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.GameKit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
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

        logger.info("Initialized GameQueueManager.");
        this.refreshQueues();
    }

    public GameQueueMember addToQueue(Player player, GameQueue gameQueue) {
        if(getQueue(player) == null) {
            GameQueueMember gqm;
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

            if(gameQueue.getType().equals(GameQueue.Type.RANKED)) {
                gqm = new GameQueueMember(player.getUniqueId(), player.getName(), profile.getProfileElo().getRatings().get(gameQueue.getGameKit()));
            } else {
                gqm = new GameQueueMember(player.getUniqueId(), player.getName());
            }

            gqm.setQueue(gameQueue);
            gameQueue.getQueueMembers().add(gqm);

            GameKit kit = gameQueue.getGameKit();
            profile.playerUpdate(false);
            player.sendMessage(ChatColor.GREEN + "You have joined the queue for " + gameQueue.getType().name().toLowerCase() + " " + ChatColor.WHITE + kit.getDisplayName() + ChatColor.GREEN + ".");

            return gqm;
        }

        return null;
    }

    public GameQueueMember addToQueue(Player player, QueueableMinigame.Type minigameType, GameQueue.Type queueType) {
        GameQueue queue = getQueue(minigameType, queueType);

        if(getQueue(player) != null) return null;

        GameQueueMember gqm;
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        gqm = new GameQueueMember(player.getUniqueId(), player.getName());

        gqm.setQueue(queue);
        queue.getQueueMembers().add(gqm);

        profile.playerUpdate(false);
        player.sendMessage(ChatColor.GREEN + "You have joined the queue for " + queue.getType().name().toLowerCase() + " " + ChatColor.WHITE + queue.getMinigameType().toString() + ChatColor.GREEN + ".");

        return gqm;
    }

    public GameQueueMember addToQueue(Player player, GameKit kit, GameQueue.Type queueType) {
        GameQueue queue = getQueue(kit, queueType);
        return addToQueue(player, queue);
    }

    public boolean removeFromQueue(Player player) {
        return removeFromQueue(player.getUniqueId());
    }

    public boolean removeFromQueue(UUID uuid) {
        GameQueue queue = getQueue(uuid);
        if(queue != null) {
            GameQueueMember gqm = findQueueMember(queue, uuid);
            queue.getQueueMembers().remove(gqm);

            Player player = gqm.getPlayer();
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "You have left the queue.");
            profile.playerUpdate(false);
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

    public GameQueue getQueue(GameKit kit, GameQueue.Type queueType) {
        for(GameQueue q : getGameQueues()) {
            if(q.getGameKit() != null && q.getGameKit().equals(kit) && q.getType().equals(queueType)) {
                return q;
            }
        }

        return null;
    }

    public GameQueue getQueue(QueueableMinigame.Type minigameType, GameQueue.Type queueType) {
        for(GameQueue q : getGameQueues()) {
            if(q.getMinigameType() != null && q.getMinigameType().equals(minigameType) && q.getType().equals(queueType)) {
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

    public int getTotalInQueue(GameQueue.Type queueType) {
        int i = 0;
        for(GameQueue q : gameQueues) {
            if(queueType.equals(q.getType())) {
                i += q.getQueueMembers().size();
            }
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

        int queues = 0;

        for(GameKit gameKit : GameKit.values()) {
            if(gameKit.isDuelKit()) {
                GameQueue queue = new GameQueue(plugin, gameKit, GameQueue.Type.UNRANKED);
                addQueue(queue);

                queue.startQueue();

                queues++;

                if(gameKit.isRanked()) {
                    GameQueue rankedQueue = new GameQueue(plugin, gameKit, GameQueue.Type.RANKED);
                    addQueue(rankedQueue);

                    rankedQueue.startQueue();

                    queues++;
                }
            }
        }

        for(QueueableMinigame.Type minigameType : QueueableMinigame.Type.values()) {
            GameQueue queue = new GameQueue(plugin, minigameType, GameQueue.Type.UNRANKED);
            queue.startQueue();
            addQueue(queue);
            queues++;
        }

        logger.info(queues + " GameQueue(s) have been refreshed.");
    }
}
