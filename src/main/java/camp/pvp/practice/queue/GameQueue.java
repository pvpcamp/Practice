package camp.pvp.practice.queue;

import camp.pvp.practice.games.impl.Duel;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.kits.DuelKit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

@Getter @Setter
public class GameQueue {

    public enum Type {
        UNRANKED, RANKED, PRIVATE, TOURNAMENT;

        @Override
        public String toString() {
            switch(this) {
                case UNRANKED:
                    return "Unranked";
                case RANKED:
                    return "Ranked";
                case PRIVATE:
                    return "Private Game";
                case TOURNAMENT:
                    return "Tournament";
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

    public int getPlaying() {
        int i = 0;
        for(Game game : getPlugin().getGameManager().getActiveGames()) {
            if(game instanceof Duel && game.getKit().equals(duelKit) && ((Duel) game).getQueueType().equals(getType())) {
                i += game.getAlive().size();
            }
        }

        return i;
    }

    public void startQueue() {
        switch(type) {
            case UNRANKED:
                queueTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                    if (queueMembers.size() > 1) {
                        final GameQueueMember member1, member2;
                        member1 = queueMembers.poll();
                        member2 = queueMembers.poll();

                        Duel duel = new Duel(plugin, UUID.randomUUID());

                        duel.setQueueType(type);
                        duel.setKit(duelKit);

                        duel.join(member1.getPlayer());
                        duel.join(member2.getPlayer());

                        duel.start();
                    }
                }, 5, 5);
                break;
            case RANKED:
                queueTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                    if(queueMembers.size() > 1) {
                        for(GameQueueMember member1 : queueMembers) {
                            for(GameQueueMember member2 : queueMembers) {
                                if(member1 != member2) {
                                    if(member2.getEloLow() <= member1.getElo() && member2.getEloHigh() >= member1.getElo()) {
                                        if (member1.getEloLow() <= member2.getElo() && member1.getEloHigh() >= member2.getElo()) {
                                            queueMembers.remove(member1);
                                            queueMembers.remove(member2);
                                            member1.getQueueUpdater().cancel();
                                            member2.getQueueUpdater().cancel();

                                            Duel duel = new Duel(plugin, UUID.randomUUID());

                                            duel.setQueueType(type);
                                            duel.setKit(duelKit);

                                            duel.join(member1.getPlayer());
                                            duel.join(member2.getPlayer());

                                            duel.start();
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }, 5, 5);
        }
    }

    public void stopQueue() {
        queueTask.cancel();

        for(GameQueueMember gqm : queueMembers) {
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(gqm.getUuid());
            profile.playerUpdate(true);
        }

        plugin.getGameQueueManager().removeQueue(this);
    }
}
