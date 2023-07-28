package camp.pvp.practice.games.impl.events;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.PostGameInventory;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.PlayerUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class SumoEvent extends GameEvent {

    private BukkitTask roundStartingTask, roundEndingTask;

    public SumoEvent(Practice plugin, UUID uuid) {
        super(plugin, uuid);
        this.setKit(DuelKit.SUMO);
    }

    public void start() {
        List<Arena> list = new ArrayList<>();
        if(getArena() == null) {
            for(Arena a : getPlugin().getArenaManager().getArenas()) {
                if(a.isEnabled() && a.getType().equals(Arena.Type.EVENT_SUMO)) {
                    list.add(a);
                }
            }

            Collections.shuffle(list);
            if(!list.isEmpty()) {
                this.setArena(list.get(0));
            } else {
                this.forceEnd();
            }
        }

        this.setState(State.STARTING);

        timer = 60;
        List<Integer> times = Arrays.asList(120, 105, 90, 75, 60, 45, 30, 15, 5);
        this.startingTimer = Bukkit.getScheduler().runTaskTimer(getPlugin(), new Runnable() {
            @Override
            public void run() {
                if(timer == 0) {
                    nextRound();

                    startingTimer.cancel();
                } else {
                    if(times.contains(timer)) {
                        joinMessage();
                    }

                    timer--;
                }
            }
        }, 0, 20);
    }

    @Override
    public void end() {
        setState(State.ENDED);
        if(getAlive().size() == 1) {
            String winner = new ArrayList<>(getAlive().values()).get(0).getName();
            this.announceAll(
                    " ",
                    "&6[Sumo Event] &f" + winner + "&6 has won the event!",
                    " ");
        }

        if(this.getStartingTimer() != null) {
            getStartingTimer().cancel();
        }

        if(this.getEndingTimer() != null) {
            getEndingTimer().cancel();
        }

        if(roundStartingTask != null) {
            roundStartingTask.cancel();
        }

        if(roundEndingTask != null) {
            roundEndingTask.cancel();
        }

        for(Player player : getAllPlayers()) {
            GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            profile.setGame(null);
            profile.playerUpdate(true);
        }

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();
    }

    public void nextRound() {
        if(this.round == 0 && this.getAlive().size() < 2) {
            this.forceEnd();
            return;
        }

        if(!state.equals(State.NEXT_ROUND_STARTING)) {
            setState(State.NEXT_ROUND_STARTING);

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

            this.announce("&6Round " + round + " starting between &f" + player1.getName() + " &6and &f" + player2.getName() + "&6.");

            timer = 3;
            roundStartingTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (timer == 0) {
                        for(Player p : SumoEvent.this.getAllPlayers()) {
                            if(p != null) {
                                if(getCurrentPlayersPlaying().contains(p)) {
                                    p.removePotionEffect(PotionEffectType.JUMP);
                                    p.playSound(p.getLocation(), Sound.FIREWORK_BLAST, 1, 1);
                                }

                                p.sendMessage(ChatColor.GREEN + "Round " + getRound() + " has started.");
                            }
                        }

                        setState(Game.State.ACTIVE);

                        cancel();
                        return;
                    } else {
                        if (timer > 0) {
                            for (Player p : getAllPlayers()) {
                                if(getCurrentPlayersPlaying().contains(p)) {
                                    p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                                }

                                p.sendMessage(ChatColor.GREEN.toString() + timer + "...");
                            }
                        }
                    }

                    timer -= 1;
                }
            }.runTaskTimer(getPlugin(), 20, 20);
        }
    }

    public void endRound() {
        if (this.roundStartingTask != null) {
            this.roundStartingTask.cancel();
        }

        for (Player p : getCurrentPlayersPlaying()) {
            p.teleport(arena.getPositions().get("lobby").getLocation());
        }

        for(GameParticipant part : getCurrentPlaying().values()) {
            part.setCurrentlyPlaying(false);
        }

        setState(State.ROUND_ENDED);

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

        roundEndingTask = new BukkitRunnable() {
            int i = 3;

            @Override
            public void run() {
                if (i == 0) {
                    if (!getState().equals(State.ENDED)) {
                        nextRound();
                    }

                    this.cancel();
                } else {
                    i -= 1;
                }
            }
        }.runTaskTimer(getPlugin(), 0, 20);
    }

    @Override
    public void eliminate(Player player, boolean leftGame) {
        GameParticipant participant = getParticipants().get(player.getUniqueId());
        GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        if(participant != null) {
            switch(this.getState()) {
                case STARTING:
                    participants.remove(participant.getUuid());
                    profile.setGame(null);
                    profile.playerUpdate(true);
                    getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

                    announce("&6[Event] &f" + player.getName() + "&a left the event.");
                    break;
                case NEXT_ROUND_STARTING:
                case ACTIVE:
                    participant.eliminate();
                    participant.clearCooldowns();

                    spectateStart(player);

                    announce("&f" + participant.getName() + "&a has been eliminated. &7(" + this.getAlive().size() + "/" + this.getParticipants().size() + ")");

                    endRound();
                    break;
                default:
                    announce("&f" + participant.getName() + "&a left the event. &7(" + this.getAlive().size() + "/" + this.getParticipants().size() + ")");

                    participant.eliminate();
                    participant.clearCooldowns();

                    profile.playerUpdate(true);

                    getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();
                    break;
            }
        }

        if(!getState().equals(State.STARTING)) {
            if (getAlive().size() < 2) {
                this.end();
            }
        }
    }

    @Override
    public GameParticipant join(Player player) {
        GameParticipant participant = super.join(player);

        player.teleport(getArena().getPositions().get("lobby").getLocation());
        PlayerUtils.reset(player);

        this.announce("&6[Event] &f" + player.getName() + "&a joined the event.");

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

        return participant;
    }

    @Override
    public void spectateStart(Player player) {
        super.spectateStart(player, getArena().getPositions().get("lobby").getLocation());
    }

    @Override
    public void spectateStartRandom(Player player) {
        spectateStart(player);
    }

    @Override
    public void spectateStart(Player player, Location location) {
        spectateStart(player);
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

    @Override
    public List<Player> getCurrentPlayersPlaying() {
        List<Player> players = new ArrayList<>();
        for(Map.Entry<UUID, GameParticipant> entry : getCurrentPlaying().entrySet()) {
            players.add(Bukkit.getPlayer(entry.getKey()));
        }

        return players;
    }

    public void joinMessage() {
        GameProfileManager gpm = getPlugin().getGameProfileManager();
        for(Player player : Bukkit.getOnlinePlayers()) {
            GameProfile profile = gpm.getLoadedProfiles().get(player.getUniqueId());
            if(profile != null && profile.isTournamentNotifications()) {
                String[] strings = new String[] {
                        " ",
                        Colors.get("&6&lEvent"),
                        Colors.get(" &7● &6Event: &fSumo"),
                        Colors.get(" &7● &6Starting In: &f" + timer + " seconds"),
                        Colors.get(" &7● &6Players: &f" + getParticipants().size())
                };

                player.sendMessage(strings);

                TextComponent msg = new TextComponent(Colors.get("&6[Click to join]"));

                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event join"));
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Colors.get("&aClick to join the event!")).create()));

                player.spigot().sendMessage(msg);
                player.sendMessage(" ");
            }
        }
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();
        List<Player> ps = getCurrentPlayersPlaying();
        lines.add(" &7● &6Event: &fSumo");
        switch(this.getState()) {
            case STARTING:
                lines.add(" &7● &6Players: &f" + this.getAlive().size());
                lines.add(" &7● &6Starting In: &f" + (timer + 1) + "s");
                break;
            case NEXT_ROUND_STARTING:
                lines.add(" &7● &6Players: &f" + this.getAlive().size() + "/" + this.getParticipants().size());
                lines.add(" &7● &6Round: &f" + getRound());
                lines.add(" &7● &6Starting In: &f" + (timer + 1) + "s");
                lines.add("&7&m------------------");
                lines.add("&a" + ps.get(0).getName() + " &f" + PlayerUtils.getPing(ps.get(0)) + " ms");
                lines.add("&a" + ps.get(1).getName() + " &f" + PlayerUtils.getPing(ps.get(1)) + " ms");
                break;
            case ACTIVE:
                lines.add(" &7● &6Players: &f" + this.getAlive().size() + "/" + this.getParticipants().size());
                lines.add(" &7● &6Round: &f" + getRound());
                lines.add("&7&m------------------");
                lines.add("&a" + ps.get(0).getName() + " &f" + PlayerUtils.getPing(ps.get(0)) + " ms");
                lines.add("&a" + ps.get(1).getName() + " &f" + PlayerUtils.getPing(ps.get(1)) + " ms");
                break;
            case ROUND_ENDED:
                lines.add(" &7● &6Players: &f" + this.getAlive().size() + "/" + this.getParticipants().size());
                lines.add(" &7● &6Round: &f" + getRound());
                break;
            case ENDED:
                lines.add("ended");
            default:
                lines.add("In Development");
        }

        return lines;
    }

    @Override
    public List<String> getSpectatorScoreboard(GameProfile profile) {
        return getScoreboard(profile);
    }

    @Override
    public boolean seeEveryone() {
        return true;
    }
}
