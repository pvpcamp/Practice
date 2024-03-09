package camp.pvp.practice.games.tournaments;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.impl.Duel;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.utils.Colors;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter @Setter
public class Tournament {

    public enum State {
        INACTIVE, STARTING, NEXT_ROUND_STARTING, IN_GAME, ENDED;

        @Override
        public String toString() {
            switch(this) {
                case INACTIVE:
                    return "Inactive";
                case STARTING:
                    return "Starting";
                case NEXT_ROUND_STARTING:
                    return "Next Round Starting";
                case IN_GAME:
                    return "In Game";
                default:
                    return "Ended";
            }
        }
    }

    private Practice plugin;
    private final GameKit gameKit;
    private final int teamSize, maxPlayers;
    private int currentRound, timer;
    private State state;
    private List<Game> games;
    private List<TournamentMatch> queuedGames;
    private Map<UUID, TournamentParticipant> tournamentParticipants;
    private BukkitTask startingTimer, roundStartingTimer;
    public Tournament(Practice plugin, GameKit gameKit, int teamSize, int maxPlayers) {
        this.plugin = plugin;
        plugin.getGameManager().setTournament(this);

        this.currentRound = 0;
        this.timer = 0;
        this.gameKit = gameKit;
        this.teamSize = teamSize;
        this.maxPlayers = maxPlayers;
        this.games = new ArrayList<>();
        this.queuedGames = new ArrayList<>();
        this.tournamentParticipants = new HashMap<>();

        this.state = State.INACTIVE;
    }

    public TournamentParticipant join(Player player) {
        if(this.getState().equals(State.INACTIVE) || this.getState().equals(State.STARTING)) {
            if(this.maxPlayers > this.tournamentParticipants.size()) {
                TournamentParticipant participant = new TournamentParticipant(player.getUniqueId(), player.getName());
                GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

                profile.setTournament(this);
                profile.givePlayerItems();

                this.getTournamentParticipants().put(player.getUniqueId(), participant);

                announce(player.getName() + "&a joined the tournament. &7(" + this.getTournamentParticipants().size() + "/" + this.getMaxPlayers() + ")");

                return participant;
            } else {
                player.sendMessage(ChatColor.RED + "This tournament is currently full.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "This tournament has already started.");
        }
        return null;
    }

    public void leave(Player player) {
        TournamentParticipant participant = this.getTournamentParticipants().get(player.getUniqueId());
        if(participant != null) {
            switch(this.getState()) {
                case INACTIVE:
                case STARTING:

                    announce(player.getName() + "&a left the tournament. &7(" + (this.getTournamentParticipants().size() - 1) + "/" + this.getMaxPlayers() + ")");

                    GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
                    profile.setTournament(null);
                    profile.playerUpdate(false);

                    this.getTournamentParticipants().remove(player.getUniqueId());
                    break;
                case NEXT_ROUND_STARTING:
                case IN_GAME:
                    eliminate(player);
                    break;
            }
        }
    }

    public void eliminate(Player player) {
        TournamentParticipant participant = this.getTournamentParticipants().get(player.getUniqueId());
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        if(participant != null) {
            participant.setEliminated(true);
            announceAll("&6[Tournament] " + player.getName() + "&a has been eliminated. &7(" + this.getAlive().size() + "/" + this.getTournamentParticipants().size() + ")");

            profile.setTournament(null);

            if(profile.getState().isLobby()) profile.playerUpdate(false);

            Bukkit.getScheduler().runTaskLater(plugin, ()-> {
                if (this.getAlive().size() < 2) {
                    this.end();
                    return;
                }

                if (this.getActiveGames().isEmpty() && this.getState().equals(State.IN_GAME)) {
                    this.nextRound();
                }
            }, 1);
        }
    }

    public void start() {

        setState(State.STARTING);

        getPlugin().getGameProfileManager().refreshLobbyItems();

        timer = 60;
        startingTimer = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                List<Integer> times = Arrays.asList(120, 105, 90, 75, 60, 30, 15, 5);
                if(timer == 0) {
                    nextRound();

                    startingTimer.cancel();
                } else {
                    if (times.contains(timer)) {
                        joinMessage();
                    }
                    timer--;
                }
            }
        }, 0, 20);
    }

    public void nextRound() {

        setState(State.NEXT_ROUND_STARTING);

        if(this.getCurrentRound() == 0) {
            if(this.getTournamentParticipants().size() < 2) {
                announce("&cNot enough players joined the tournament, cancelling.");
                for(TournamentParticipant p : Tournament.this.getTournamentParticipants().values()) {
                    GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(p.getUuid());
                    profile.setTournament(null);
                    profile.playerUpdate(true);

                    setState(State.ENDED);
                }

                getTournamentParticipants().clear();
                plugin.getGameManager().setTournament(null);
                return;
            }
        }

        this.currentRound++;

        List<TournamentParticipant> shuffledParticipants = new ArrayList<>(getAlive());
        Collections.shuffle(shuffledParticipants);

        Queue<TournamentParticipant> queuedParticipants = new LinkedList<>(shuffledParticipants);
        while(!queuedParticipants.isEmpty()) {
            TournamentParticipant participantOne = null, participantTwo = null;
            participantOne = queuedParticipants.poll();
            participantTwo = queuedParticipants.poll();

            if(participantTwo != null) {
                TournamentMatch match = null;
                switch(teamSize) {
                    case 1:
                        Duel duel = new Duel(plugin, UUID.randomUUID());
                        duel.setKit(gameKit);
                        duel.setTournament(this);
                        duel.setQueueType(GameQueue.Type.TOURNAMENT);

                        match = new TournamentMatch(duel, participantOne, participantTwo);
                        this.getQueuedGames().add(match);
                        this.getGames().add(duel);

                        String matchMessage = "&6[Tournament Match] Your next match: &f" + participantOne.getName() + " &cvs. &f" + participantTwo.getName();
                        participantOne.getPlayer().sendMessage(Colors.get(matchMessage));
                        participantTwo.getPlayer().sendMessage(Colors.get(matchMessage));
                }
            } else {
                Player player = participantOne.getPlayer();
                player.sendMessage(ChatColor.GREEN + "You will not have an opponent this round.");
            }
        }

        timer = 30;
        roundStartingTimer = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                List<Integer> times = Arrays.asList(30, 15, 10, 5,4,3,2,1);
                if(timer == 0) {
                    startGames();
                    roundStartingTimer.cancel();
                } else {
                    if(times.contains(timer)) {
                        announce("&eRound " + currentRound + " starting in " + timer + " second" + (timer == 1 ? "" : "s") + ".");
                    }
                    timer--;
                }
            }
        }, 0, 20);
    }

    public void startGames() {
        for(TournamentMatch match : getQueuedGames()) {
            match.startGame();
        }

        setState(State.IN_GAME);

        announceAll("&6[Tournament] &eRound " + currentRound + " has started.");

        getQueuedGames().clear();
    }

    public void end() {
        TournamentParticipant participant = this.getAlive().get(0);

        announceAll(
                " ",
                "&6&lTournament",
                " &7● &6Winner: &f" + participant.getName(),
                " ");


        for(TournamentParticipant p : getAlive()) {
            Player player = p.getPlayer();
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            profile.setTournament(null);
        }

        for(TournamentMatch match : getQueuedGames()) {
            match.getGame().setState(Game.State.ENDED);
        }

        getQueuedGames().clear();

        plugin.getGameManager().setTournament(null);

        setState(State.ENDED);

        if(getStartingTimer() != null) getStartingTimer().cancel();

        if(getRoundStartingTimer() != null) getRoundStartingTimer().cancel();

        plugin.getGameProfileManager().refreshLobbyItems();
    }

    public void forceEnd() {
        for(Game game : getActiveGames()) {
            game.forceEnd(false);
        }

        for(TournamentParticipant p : getAlive()) {
            Player player = p.getPlayer();
            GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
            profile.setTournament(null);
        }

        if(getStartingTimer() != null) {
            getStartingTimer().cancel();
        }

        if(getRoundStartingTimer() != null) {
            getRoundStartingTimer().cancel();
        }

        announce("&4This tournament has been forcefully ended.");

        this.setState(State.ENDED);

        plugin.getGameProfileManager().refreshLobbyItems();
    }

    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();
        lines.add("&6Tournament");
        switch(this.getState()) {
            case STARTING:
                lines.add(" &7● &6Starting In: &f" + this.getTimer() + "s");
                lines.add(" &7● &6Players: &f" + this.getTournamentParticipants().size() + "/" + this.getMaxPlayers());
                if(teamSize == 2) {
                    // TODO: 2v2 Tournaments.
                }
                break;
            case NEXT_ROUND_STARTING:
                lines.add(" &7● &6Round: &f" + this.getCurrentRound());
                lines.add(" &7● &6Starting In: &f" + this.getTimer() + "s");
                lines.add(" &7● &6Players Left: &f" + this.getAlive().size() + "/" + this.getTournamentParticipants().size());
                switch(teamSize) {
                    case 1:
                        TournamentParticipant opponent = null;
                        for(TournamentMatch match : getQueuedGames()) {
                            if(match.getParticipants().get(profile.getUuid()) != null) {
                                for(TournamentParticipant p : match.getParticipants().values()) {
                                    if(!p.getUuid().equals(profile.getUuid())) {
                                        opponent = p;
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                        lines.add(" ");
                        lines.add("&6Next Round:");

                        if(opponent != null) {
                            lines.add("&e" + profile.getName());
                            lines.add("&7&ovs.");
                            lines.add("&c" + opponent.getName());
                        } else {
                            lines.add("&f&oBYE");
                        }
                }
                break;
            case IN_GAME:
                lines.add(" &7● &6Round: &f" + this.getCurrentRound());
                lines.add(" &7● &6Players Left: &f" + this.getAlive().size() + "/" + this.getTournamentParticipants().size());
                lines.add(" &7● &6Active Games: &f" + this.getActiveGames().size());
                break;
        }

        return lines;
    }

    public List<Game> getActiveGames() {
        List<Game> activeGames = new ArrayList<>();
        for(Game game : this.getGames()) {
            if(!game.getState().equals(Game.State.ENDED)) {
                activeGames.add(game);
            }
        }

        return activeGames;
    }

    public List<TournamentParticipant> getAlive() {
        List<TournamentParticipant> participants = new ArrayList<>();
        for(TournamentParticipant participant : getTournamentParticipants().values()) {
            if(!participant.isEliminated()) {
                participants.add(participant);
            }
        }

        return participants;
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        for(TournamentParticipant participant : getTournamentParticipants().values()) {
            if(!participant.isEliminated()) {
                players.add(participant.getPlayer());
            }
        }

        return players;
    }

    public void announce(String s) {
        for(Player player : getAllPlayers()) {
            player.sendMessage(Colors.get("&6[Tournament] &f" + s));
        }
    }

    public void joinMessage() {
        GameProfileManager gpm = plugin.getGameProfileManager();
        for(Player player : Bukkit.getOnlinePlayers()) {
            GameProfile profile = gpm.getLoadedProfiles().get(player.getUniqueId());
            if(profile != null && profile.isTournamentNotifications()) {
                String[] strings = new String[]{
                        " ",
                        Colors.get("&6&lTournament"),
                        Colors.get(" &7● &6Starting In: &f" + timer + "s"),
                        Colors.get(" &7● &6Kit: &f" + getGameKit().getDisplayName()),
                        Colors.get(" &7● &6Players: &f" + getTournamentParticipants().size())
                };

                player.sendMessage(strings);

                TextComponent msg = new TextComponent(Colors.get("&6[Click to join]"));

                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tournament join"));
                msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Colors.get("&aClick to join the tournament!")).create()));

                player.spigot().sendMessage(msg);
                player.sendMessage(" ");
            }
        }
    }

    public void announceAll(String... strings) {
        GameProfileManager gpm = plugin.getGameProfileManager();
        for(Player p : Bukkit.getOnlinePlayers()) {
            GameProfile profile = gpm.getLoadedProfiles().get(p.getUniqueId());
            if(profile != null) {
                if(getAllPlayers().contains(p) || profile.isTournamentNotifications()) {
                    for (String s : strings) {
                        p.sendMessage(Colors.get(s));
                    }
                }
            }
        }
    }
}
