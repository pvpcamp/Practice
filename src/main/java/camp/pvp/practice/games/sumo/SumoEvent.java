package camp.pvp.practice.games.sumo;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.TimeUtil;
import lombok.Data;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Data
public class SumoEvent {

    private final Practice plugin;
    private Map<UUID, EventParticipant> participants;
    private SumoEventDuel currentDuel;
    private Arena arena;
    private State state;
    private int round, timer;
    private BukkitTask startingTimer;

    public SumoEvent(Practice plugin) {
        this.plugin = plugin;
        this.participants = new HashMap<>();
        this.state = State.INACTIVE;
        this.arena = Practice.getInstance().getArenaManager().selectRandomArena(Arena.Type.EVENT_SUMO);
        plugin.getGameManager().setSumoEvent(this);
    }

    public void start() {

        state = State.STARTING;

        if(arena == null) {
            announceAll("&cThere are no arenas available for the event.");
            forceEnd();
            return;
        }

        getPlugin().getGameProfileManager().refreshLobbyItems();

        timer = 60;

        startingTimer = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if(timer == 0) {
                startingTimer.cancel();

                if(getTotalParticipants() < 2) {
                    forceEnd();
                    return;
                }

                nextRound();
                return;
            }

            if(timer % 15 == 0) {
                joinMessage();
            }

            if(timer % 10 == 0 || timer < 5) {
                announce("&aEvent starting in &f" + timer + " &asecond(s).");
            }

            timer--;
        }, 0, 20);
    }

    public void nextRound() {
        round++;

        currentDuel = null;

        setState(State.IN_GAME);

        List<EventParticipant> nextRoundParticipants = getNextRoundParticipants();
        if(nextRoundParticipants == null || nextRoundParticipants.size() < 2) {
            end();
            return;
        }

        announce("&aRound &f" + round + " &ahas started.");

        SumoEventDuel duel = new SumoEventDuel(plugin, this);
        duel.join(nextRoundParticipants.get(0).getPlayer());
        duel.join(nextRoundParticipants.get(1).getPlayer());

        currentDuel = duel;

        duel.start();

    }

    public void forceEnd() {

        setState(State.ENDED);

        announceAll("&cThe event has been force ended.");

        for(EventParticipant participant : participants.values()) {
            leave(participant.getPlayer());
        }

        plugin.getGameManager().setSumoEvent(null);
    }

    public void end() {

        setState(State.ENDED);

        announceAll(
                " ",
                "&6&lSumo Event",
                " &7● &6Winner: &f" + getWinner().getName(),
                ""
        );

        for(EventParticipant participant : new ArrayList<>(participants.values())) {
            leave(participant.getPlayer());
        }

        plugin.getGameManager().setSumoEvent(null);
    }

    public void join(Player player) {

        EventParticipant participant = participants.get(player.getUniqueId());

        if(participant == null) {
            participant = new EventParticipant(player);
            participant.setPlaying(state.equals(State.STARTING));
        };

        participant.setAlive(state.equals(State.STARTING));

        participants.put(player.getUniqueId(), participant);

        GameProfile profile = participant.getProfile();
        profile.setSumoEvent(this);
        profile.playerUpdate(true);

        if(state.equals(State.STARTING)) {
            announce("&f" + player.getName() + " &ahas joined the event.");
        }

        plugin.getGameProfileManager().updateGlobalPlayerVisibility();
    }

    public void leave(Player player) {
        eliminate(player, true);
    }

    public void eliminate(Player player, boolean leftGame) {

        EventParticipant participant = participants.get(player.getUniqueId());

        if(participant == null) return;

        GameProfile profile = participant.getProfile();

        String message = leftGame ? " &ahas left the event." : " &ahas been eliminated.";

        if(!state.equals(State.ENDED) && participant.isAlive()) announce("&f" + player.getName() + message);

        participant.setAlive(false);

        switch(state) {
            case STARTING, ENDED -> participants.remove(player.getUniqueId());
            case ENDING -> {

            }
            default -> {
                if(getCurrentDuel() != null && getCurrentDuel().getCurrentPlaying().containsKey(player.getUniqueId())) {
                    nextRound();
                }
            }
        }

        if(leftGame) {
            profile.setSumoEvent(null);
            participant.setActive(false);
        }

        profile.playerUpdate(true);

        Practice.getInstance().getGameProfileManager().updateGlobalPlayerVisibility();

    }

    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();

        lines.add("&6Sumo Event");

        switch(state) {
            case STARTING -> {
                lines.add("&7● &6Players: &f" + getAliveCount());
                lines.add("&7● &6Starting In: &f" + (timer + 1) + "s");
            }
            case IN_GAME -> {
                lines.add("&7● &6Players: &f" + getAliveCount() + "/" + getTotalParticipants());
                lines.add("&7● &6Round: &f" + round);
                if(getCurrentDuel() != null) {

                    if(profile.isSidebarShowDuration() && getCurrentDuel().getState().equals(Game.State.ACTIVE)) {
                        lines.add("&7● &6Duration: &f" + TimeUtil.get(new Date(), getCurrentDuel().getStarted()));
                    }

                    if(profile.isSidebarShowLines()) {
                        lines.add("&7&m------------------");
                    } else {
                        lines.add(" ");
                    }

                    for(Player player : getCurrentDuel().getCurrentPlayersPlaying()) {
                        lines.add("&7> &f" + player.getName());
                    }
                } else {

                }
            }
            case ENDING -> {

            }
        }

        return lines;
    }

    private List<EventParticipant> getNextRoundParticipants() {
        List<EventParticipant> list = new ArrayList<>(getAlive().values());

        if(list.size() < 2) return null;

        list.sort(Comparator.comparingInt(EventParticipant::getMatches).reversed());

        List<EventParticipant> nextRound = new ArrayList<>();
        nextRound.add(list.get(0));
        nextRound.add(list.get(1));

        return nextRound;
    }

    private EventParticipant getWinner() {
        return getAlive().values().stream().findFirst().orElse(null);
    }

    /***
     * Provides a map of only the currently alive participants in the event.
     */
    public Map<UUID, EventParticipant> getAlive() {
        Map<UUID, EventParticipant> map = new HashMap<>();
        for(EventParticipant participant : participants.values()) {
            if(participant.isAlive()) {
                map.put(participant.getUuid(), participant);
            }
        }
        return map;
    }

    /***
     * Provides a map of all participants that are either alive, playing, or just spectating the event.
     */
    public Map<UUID, EventParticipant> getActiveParticipants() {
        Map<UUID, EventParticipant> map = new HashMap<>();
        for(EventParticipant participant : participants.values()) {
            if(participant.isActive()) {
                map.put(participant.getUuid(), participant);
            }
        }

        return map;
    }

    public List<Player> getActivePlayers() {
        List<Player> list = new ArrayList<>();
        for(EventParticipant participant : getActiveParticipants().values()) {
            list.add(participant.getPlayer());
        }
        return list;
    }

    /***
     * Gets the amount of players that are currently alive in the event.
     */
    private int getAliveCount() {
        return getAlive().size();
    }

    /***
     * Gets the total amount of players that actually played in the event.
     */
    private int getTotalParticipants() {
        int i = 0;

        for(EventParticipant participant : participants.values()) {
            if(participant.isPlaying()) {
                i++;
            }
        }

        return i;
    }

    public void announce(String... messages) {
        for(EventParticipant participant : getActiveParticipants().values()) {
            participant.getPlayer().sendMessage(Colors.get(messages));
        }
    }

    public void announceAll(String... messages) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Colors.get(messages));
        }
    }

    public void joinMessage() {
        GameProfileManager gpm = plugin.getGameProfileManager();
        for(Player player : Bukkit.getOnlinePlayers()) {
            GameProfile profile = gpm.getLoadedProfiles().get(player.getUniqueId());
            if(profile != null && profile.isTournamentNotifications()) {
                String[] strings = new String[]{
                        " ",
                        Colors.get("&6&lSumo Event"),
                        Colors.get(" &7● &6Starting In: &f" + timer + "s"),
                        Colors.get(" &7● &6Players: &f" + getTotalParticipants())
                };

                player.sendMessage(strings);

                TextComponent msg = new TextComponent(Colors.get("&6[Click to join!]"));

                msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/event join"));

                player.spigot().sendMessage(msg);
                player.sendMessage(" ");
            }
        }
    }

    public enum State {
        INACTIVE, STARTING, IN_GAME, ENDING, ENDED;

        @Override
        public String toString() {
            switch(this) {
                case INACTIVE:
                    return "Inactive";
                case STARTING:
                    return "Starting";
                case IN_GAME:
                    return "In Game";
                default:
                    return "Ended";
            }
        }
    }
}
