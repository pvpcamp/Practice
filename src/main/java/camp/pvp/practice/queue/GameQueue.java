package camp.pvp.practice.queue;

import camp.pvp.practice.games.impl.Duel;
import camp.pvp.practice.games.minigames.OneInTheChamberMinigame;
import camp.pvp.practice.games.minigames.Minigame;
import camp.pvp.practice.games.minigames.SkywarsMinigame;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.utils.Colors;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

@Getter @Setter
public class GameQueue {

    public enum GameType {
        DUEL, MINIGAME;
    }

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

        public ChatColor getColor() {
            switch (this) {
                case RANKED:
                    return ChatColor.GOLD;
                case PRIVATE:
                    return ChatColor.AQUA;
                case TOURNAMENT:
                    return ChatColor.LIGHT_PURPLE;
                default:
                    return ChatColor.YELLOW;
            }
        }
    }

    private final Practice plugin;
    private final GameType gameType;
    private final Type type;
    private GameKit gameKit;
    private Minigame.Type minigameType;
    private Queue<GameQueueMember> queueMembers;
    private BukkitTask queueTask;
    private boolean available;

    public GameQueue(Practice plugin, GameKit gameKit, Type type) {
        this.plugin = plugin;
        this.gameType = GameType.DUEL;
        this.gameKit = gameKit;
        this.type = type;
        this.queueMembers = new LinkedList<>();
        this.available = true;
    }

    public GameQueue(Practice plugin, Minigame.Type minigameType, Type type) {
        this.plugin = plugin;
        this.gameType = GameType.MINIGAME;
        this.minigameType = minigameType;
        this.type = type;
        this.queueMembers = new LinkedList<>();
        this.available = true;
    }

    public GameType getGameType() {
        return minigameType == null ? GameType.DUEL : GameType.MINIGAME;
    }

    public int getPlaying() {
        int i = 0;

        switch(gameType) {
            case DUEL -> {
                for(Game game : getPlugin().getGameManager().getActiveGames()) {
                    if(game instanceof Duel && game.getKit().equals(gameKit) && ((Duel) game).getQueueType().equals(getType())) {
                        i += game.getAlive().size();
                    }
                }
            }
            case MINIGAME -> {
                for(Game game : getPlugin().getGameManager().getActiveGames()) {
                    if(game instanceof Minigame && ((Minigame) game).getType().equals(minigameType)) {
                        i += game.getAlive().size();
                    }
                }
            }
        }

        return i;
    }

    public void startQueue() {
        switch(gameType) {
            case DUEL -> {
                switch(type) {
                    case UNRANKED:
                        queueTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                            if(queueMembers.size() < 2) {
                                return;
                            }

                            final GameQueueMember member1, member2;
                            member1 = queueMembers.poll();
                            member2 = queueMembers.poll();

                            Duel duel = new Duel(plugin, UUID.randomUUID());

                            duel.setQueueType(type);
                            duel.setKit(gameKit);

                            duel.join(member1.getPlayer());
                            duel.join(member2.getPlayer());

                            String message = Colors.get("&6&lMATCH FOUND! &r&f" + member1.getName() + " &cvs. &f" + member2.getName());
                            member1.getPlayer().sendMessage(message);
                            member2.getPlayer().sendMessage(message);

                            duel.initialize();
                        }, 0, 5);
                        break;
                    case RANKED:
                        queueTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                            if(queueMembers.size() < 2) {
                                return;
                            }

                            for (GameQueueMember member1 : queueMembers) {
                                for (GameQueueMember member2 : queueMembers) {
                                    if (member1 == member2) continue;

                                    if (member2.getEloLow() <= member1.getElo() && member2.getEloHigh() >= member1.getElo()) {
                                        if (member1.getEloLow() <= member2.getElo() && member1.getEloHigh() >= member2.getElo()) {
                                            queueMembers.remove(member1);
                                            queueMembers.remove(member2);
                                            member1.getQueueUpdater().cancel();
                                            member2.getQueueUpdater().cancel();

                                            Duel duel = new Duel(plugin, UUID.randomUUID());

                                            duel.setQueueType(type);
                                            duel.setKit(gameKit);

                                            duel.join(member1.getPlayer());
                                            duel.join(member2.getPlayer());

                                            String message = Colors.get("&6&lMATCH FOUND! &r&f" + member1.getName() + " &7(" + member1.getElo() + ") &cvs. &f" + member2.getName() + " &7(" + member2.getElo() + " ELO)");
                                            member1.getPlayer().sendMessage(message);
                                            member2.getPlayer().sendMessage(message);

                                            duel.initialize();
                                            return;
                                        }
                                    }
                                }
                            }
                        }, 0, 5);
                    }
                }
            case MINIGAME -> {
                queueTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {

                    if(queueMembers.size() < 4) {
                        return;
                    }

                    final GameQueueMember member1, member2, member3, member4;
                    member1 = queueMembers.poll();
                    member2 = queueMembers.poll();
                    member3 = queueMembers.poll();
                    member4 = queueMembers.poll();

                    Minigame minigame;

                    switch(getMinigameType()) {
                        case SKYWARS:
                            minigame = new SkywarsMinigame(plugin, UUID.randomUUID());
                            break;
                        case ONE_IN_THE_CHAMBER:
                            minigame = new OneInTheChamberMinigame(plugin, UUID.randomUUID());
                            break;
                        default:
                            minigame = null;
                            break;
                    }

                    if(minigame == null) {
                        return;
                    }

                    minigame.join(member1.getPlayer());
                    minigame.join(member2.getPlayer());
                    minigame.join(member3.getPlayer());
                    minigame.join(member4.getPlayer());

                    minigame.initialize();
                }, 5, 5);
            }
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
