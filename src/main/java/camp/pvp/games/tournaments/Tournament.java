package camp.pvp.games.tournaments;

import camp.pvp.Practice;
import camp.pvp.arenas.Arena;
import camp.pvp.games.Game;
import camp.pvp.games.impl.Duel;
import camp.pvp.kits.DuelKit;
import camp.pvp.profiles.GameProfile;
import camp.pvp.profiles.GameProfileManager;
import camp.pvp.utils.Colors;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter @Setter
public abstract class Tournament {

    public enum State {
        INACTIVE, STARTING, NEXT_ROUND_STARTING, IN_GAME, ENDED;
    }

    private Practice plugin;
    private final DuelKit duelKit;
    private final int teamSize, maxPlayers;
    private int currentRound;
    private State state;
    private List<Game> games;
    private List<TournamentMatch> queuedGames;
    private Map<UUID, TournamentParticipant> tournamentParticipants;
    private BukkitTask startingTimer, roundStartingTimer;
    public Tournament(Practice plugin, DuelKit duelKit, int teamSize, int maxPlayers) {
        this.plugin = plugin;
        plugin.getGameManager().setTournament(this);

        this.currentRound = 0;
        this.duelKit = duelKit;
        this.teamSize = teamSize;
        this.maxPlayers = maxPlayers;
        this.games = new ArrayList<>();
        this.queuedGames = new ArrayList<>();
        this.tournamentParticipants = new HashMap<>();
    }

    public TournamentParticipant join(Player player) {
        if(this.getState().equals(State.INACTIVE) || this.getState().equals(State.STARTING)) {
            if(this.maxPlayers > this.tournamentParticipants.size()) {
                TournamentParticipant participant = new TournamentParticipant(player.getUniqueId(), player.getName());
                GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

                profile.setTournament(this);
                profile.givePlayerItems();

                this.getTournamentParticipants().put(player.getUniqueId(), participant);
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
                    player.sendMessage(ChatColor.GREEN + "You have left the tournament.");
                    this.getTournamentParticipants().remove(player.getUniqueId());
                    announce(player.getName() + "&a left the tournament. &7(" + this.getTournamentParticipants().size() + ")");
                    GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
                    profile.setTournament(null);
                    profile.playerUpdate();
                    break;
                case NEXT_ROUND_STARTING:
                case IN_GAME:
                    eliminate(player, true);
                    break;
            }
        }
    }

    public void eliminate(Player player, boolean leftGame) {
        TournamentParticipant participant = this.getTournamentParticipants().get(player.getUniqueId());
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        if(participant != null) {
            participant.setEliminated(true);
            announce(player.getName() + "&a has been eliminated. &7(" + this.getAlive().size() + this.getTournamentParticipants().size());

            profile.setTournament(null);

            if(this.getAlive().size() < 2) {
                this.end();
                return;
            }

            if(this.getActiveGames().size() == 0) {
                this.nextRound();
            }
        }
    }

    public void start() {

    }

    public void nextRound() {
        this.currentRound++;

        Queue<TournamentParticipant> participants = new LinkedList<>(getAlive());
        while(participants.size() > 0) {
            TournamentParticipant participantOne = null, participantTwo = null;
            participantOne = participants.poll();
            participantTwo = participants.poll();

            if(participantTwo != null) {
                TournamentMatch match = null;
                switch(teamSize) {
                    case 1:
                        Duel duel = new Duel(plugin, UUID.randomUUID());
                        duel.setKit(duelKit);

                        match = new TournamentMatch(duel, participantOne, participantTwo);
                }
            }

        }

        roundStartingTimer = Bukkit.getScheduler().runTaskTimer(plugin, new BukkitRunnable() {
            int i = 30;
            @Override
            public void run() {
                List<Integer> times = Arrays.asList(30, 15, 10, 5,4,3,2,1);
                if(i == 0) {
                    announce("&eRound " + currentRound + " has started!");
                    startGames();
                    this.cancel();
                    return;
                }

                if(times.contains(i)) {
                    announce("&eRound " + currentRound + " starting in " + i + "second(s).");
                }
                i--;
            }
        }, 10, 10);
    }

    public void startGames() {
        for(TournamentMatch match : getQueuedGames()) {
            match.startGame();
        }

        getQueuedGames().clear();
    }

    public void end() {

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
            player.sendMessage(Colors.get("&6[Tournament] &f"));
        }
    }

    public void announceAll(String s) {
        GameProfileManager gpm = plugin.getGameProfileManager();
        for(Player player : Bukkit.getOnlinePlayers()) {
            GameProfile profile = gpm.getLoadedProfiles().get(player.getUniqueId());
            if(profile != null && profile.isTournamentNotifications()) {
                player.sendMessage(Colors.get(s));
            }
        }
    }

    public void staffAnnounce(String s) {
        for(Player player : getAllPlayers()) {
            if(player.hasPermission("practice.staff")) {
                player.sendMessage(Colors.get("&6&l[Tournament STAFF] &f"));
            }
        }
    }

    public void debugAnnounce(String s) {

    }


}
