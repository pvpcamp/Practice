package camp.pvp.practice.games.impl.events;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.kits.DuelKit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class SumoEvent extends Event {

    private BukkitTask roundStartingTask, roundEndingTask;

    public SumoEvent(Practice plugin, UUID uuid) {
        super(plugin, uuid);
        this.setKit(DuelKit.SUMO);
    }

    public void start() {

    }

    @Override
    public void eliminate(Player player, boolean leftGame) {
        super.eliminate(player, leftGame);

        if(getCurrentPlayersPlaying().contains(player)) {
            if(!getState().equals(State.ROUND_ENDED)) {
                setState(State.ROUND_ENDED);

                for(GameParticipant participant : getCurrentPlaying().values()) {
                    participant.setCurrentlyPlaying(false);
                    Player p = participant.getPlayer();
                    if(p != null) {
                        player.teleport(getArena().getPositions().get("spawn").getLocation());
                    }
                }

                if(participants.size() < 2) {
                    this.end();
                    return;
                }

                roundEndingTask = Bukkit.getScheduler().runTaskTimer(getPlugin(), new BukkitRunnable() {
                    int i = 5;

                    @Override
                    public void run() {
                        if (i == 0) {
                            nextRound();

                            this.cancel();
                        } else {
                            i -= 1;
                        }
                    }
                }, 20, 20);
            }
        }
    }

    @Override
    public void spectateStart(Player player) {
        spectateStart(player, arena.getPositions().get("spawn").getLocation());
    }

    public void nextRound() {
        if(!state.equals(State.NEXT_ROUND_STARTING)) {
            List<GameParticipant> participants = new ArrayList<>(getAlive().values());
            Collections.shuffle(participants);

            round++;

            GameParticipant player1 = participants.get(0);
            GameParticipant player2 = participants.get(1);

            ArenaPosition spawn1 = arena.getPositions().get("spawn1"), spawn2 = arena.getPositions().get("spawn2");

            Player p1 = player1.getPlayer();
            Player p2 = player2.getPlayer();

            getKit().apply(p1);
            getKit().apply(p2);

            p1.teleport(spawn1.getLocation());
            p2.teleport(spawn2.getLocation());

            player1.setCurrentlyPlaying(true);
            player2.setCurrentlyPlaying(true);

            roundStartingTask = Bukkit.getScheduler().runTaskTimer(getPlugin(), new BukkitRunnable() {
                int i = 5;
                @Override
                public void run() {
                    if (i == 0) {
                        for(Player p : SumoEvent.this.getAllPlayers()) {
                            if(p != null) {
                                p.removePotionEffect(PotionEffectType.JUMP);
                                p.playSound(p.getLocation(), Sound.FIREWORK_BLAST, 1, 1);
                                p.sendMessage(ChatColor.GREEN + "Round " + getRound() + " has started.");
                            }
                        }

                        setState(Game.State.ACTIVE);

                        this.cancel();
                    } else {
                        if (i > 0) {
                            for (Player p : getAllPlayers()) {
                                p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                                p.sendMessage(ChatColor.GREEN.toString() + i + "...");
                            }
                        }

                        i -= 1;
                    }
                }
            }, 20, 20);
        }
    }

    @Override
    public Map<UUID, GameParticipant> getCurrentPlaying() {
        Map<UUID, GameParticipant> participants = new HashMap<>();
        for(Map.Entry<UUID, GameParticipant> entry : getAlive().entrySet()) {
            if(entry.getValue().isCurrentlyPlaying()) {
                participants.put(entry.getKey(), entry.getValue());
            }
        }

        return participants;
    }
}
