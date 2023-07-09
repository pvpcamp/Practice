package camp.pvp.queue;

import camp.pvp.Practice;
import camp.pvp.games.impl.Duel;
import camp.pvp.kits.DuelKit;
import camp.pvp.profiles.GameProfile;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class GameQueue {

    public enum Type {
        UNRANKED, RANKED, PRIVATE;

        @Override
        public String toString() {
            switch(this) {
                case UNRANKED:
                    return "Unranked";
                case RANKED:
                    return "Ranked";
                case PRIVATE:
                    return "Private Game";
                default:
                    return null;
            }
        }
    }

    private final Practice plugin;
    private final DuelKit duelKit;
    private final Type type;
    private Queue<GameQueueMember> queueMembers;
    private BukkitTask queueTask;

    public GameQueue(Practice plugin, DuelKit duelKit, Type type) {
        this.plugin = plugin;
        this.duelKit = duelKit;
        this.type = type;
        this.queueMembers = new LinkedList<>();
    }

    public void startQueue() {
        switch(type) {
            case UNRANKED:
                queueTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                    if (queueMembers.size() > 1) {
                        final GameQueueMember member1, member2;
                        member1 = queueMembers.poll();
                        member2 = queueMembers.poll();

                        Duel duel = new Duel(plugin, UUID.randomUUID());

                        duel.setQueueType(type);
                        duel.setKit(duelKit);

                        duel.join(member1.getPlayer());
                        duel.join(member2.getPlayer());

                        plugin.getGameManager().addGame(duel);

                        duel.start();
                    }
                }, 5, 5);
            case RANKED:
                // TODO: Ranked Matchmaking
        }
    }

    public void stopQueue() {
        queueTask.cancel();

        for(GameQueueMember gqm : queueMembers) {
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(gqm.getUuid());
            profile.playerUpdate();
        }

        plugin.getGameQueueManager().removeQueue(this);
    }
}
