package camp.pvp.practice.games.sumo;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.utils.ClickableMessageBuilder;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.TimeUtil;
import lombok.Data;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.WordUtils;
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

            if(timer % 15 == 0 || timer == 5) {
                joinMessage();
            }

            if(timer % 10 == 0 || timer <= 5) {
                announce("&aEvent starting in &f" + timer + " &asecond" + (timer == 1 ? "" : "s") + ".");
            }

            timer--;
        }, 0, 20);
    }

    public void nextRound() {
        round++;

        if(state.equals(State.ENDED)) {
            return;
        }

        setState(State.IN_GAME);

        List<EventParticipant> nextRoundParticipants = getNextRoundParticipants();
        if(nextRoundParticipants == null || nextRoundParticipants.size() < 2) {
            end();
            return;
        }

        announce("&aRound &f" + round + " &ahas started.");

        SumoEventDuel duel = new SumoEventDuel(plugin, this);

        for(EventParticipant participant : nextRoundParticipants) {
            participant.incrementMatches();
            duel.join(participant.getPlayer());
        }

        currentDuel = duel;

        duel.initialize();
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

        if(state.equals(State.ENDED)) return;

        setState(State.ENDED);

        announceAll(
                " ",
                "&6&lSumo Event",
                " &7● &6Winner: &f" + getWinner().getName(),
                ""
        );

        for(EventParticipant participant : new ArrayList<>(getActiveParticipants().values())) {
            leave(participant.getPlayer());
        }

        plugin.getGameManager().setSumoEvent(null);
    }

    public void join(Player player) {

        EventParticipant participant = participants.get(player.getUniqueId());

        if(participant == null) {
            participant = new EventParticipant(player, this);
        }

        if(state.equals(State.IN_GAME) || state.equals(State.NEXT_ROUND_STARTING)) {
            participant.setPlayingState(EventParticipant.PlayingState.SPECTATOR);
        }

        participants.put(player.getUniqueId(), participant);

        GameProfile profile = participant.getProfile();
        profile.setSumoEvent(this);
        profile.playerUpdate(true);

        if(state.equals(State.STARTING)) {
            participant.setPlayerInEvent(true);
            announce("&f" + player.getName() + " &ahas joined the event. &7(" + getTotalParticipants() + " player" + (getTotalParticipants() == 1 ? "" : "s") + ")");
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

        participant.setPlayingState(EventParticipant.PlayingState.SPECTATOR);

        switch(state) {
            case STARTING, ENDED -> participants.remove(player.getUniqueId());
            default -> {
                if(getCurrentDuel() != null && getCurrentDuel().getParticipants().containsKey(player.getUniqueId()) && getAliveCount() > 1) {
                    setState(State.NEXT_ROUND_STARTING);
                    setCurrentDuel(null);
                    Bukkit.getScheduler().runTaskLater(plugin, this::nextRound, 40);
                }
            }
        }

        if(leftGame || state.equals(State.ENDED)) {
            profile.setSumoEvent(null);
            profile.setGame(null);
            participant.setPlayingState(EventParticipant.PlayingState.DEAD);
        }

        profile.playerUpdate(true);

        Practice.getInstance().getGameProfileManager().updateGlobalPlayerVisibility();

        if(getAliveCount() < 2 && (getState().equals(State.IN_GAME) || getState().equals(State.NEXT_ROUND_STARTING))) {
            end();
        }
    }

    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();

        lines.add("&6Sumo Event");

        switch(state) {
            case STARTING -> {
                lines.add("&7● &6Players: &f" + getAliveCount());
                lines.add("&7● &6Starting In: &f" + (timer + 1) + "s");
            }
            case IN_GAME, NEXT_ROUND_STARTING -> {
                lines.add("&7● &6Players: &f" + getAliveCount() + "/" + getTotalParticipants());
                lines.add("&7● &6Round: &f" + round);

                if(getCurrentDuel() != null && !getCurrentDuel().getState().equals(Game.State.ENDED)) {

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
                }
            }
        }

        return lines;
    }

    private List<EventParticipant> getNextRoundParticipants() {
        List<EventParticipant> list = new ArrayList<>(getAlive().values());

        if(list.size() < 2) return null;

        Collections.shuffle(list);

        list.sort(Comparator.comparingInt(EventParticipant::getMatches));

        List<EventParticipant> nextRound = new ArrayList<>();
        for(int i = 0; i < 2; i++) {
            nextRound.add(list.get(i));
        }

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
            if(participant.isPlayerInEvent()) {
                i++;
            }
        }

        return i;
    }

    public void announce(String... messages) {
        for(EventParticipant participant : getActiveParticipants().values()) {
            Player player = participant.getPlayer();
            if(player == null) continue;

            player.sendMessage(Colors.get(messages));
        }
    }

    public void announceAll(String... messages) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(Colors.get(messages));
        }
    }

    public void joinMessage() {
        ClickableMessageBuilder cmb = new ClickableMessageBuilder()
                .setLines(
                        " ",
                        "&6&lSumo Event",
                        " &7● &6Starting In: &f" + timer + "s",
                        " &7● &6Players: &f" + getTotalParticipants(),
                        "&aClick to join!",
                        " "
                )
                .setHoverMessage("&aClick to join the event!")
                .setCommand("/event join");

        for(Player player : Bukkit.getOnlinePlayers()) {
            cmb.sendToPlayer(player);
        }
    }

    public enum State {
        INACTIVE, STARTING, NEXT_ROUND_STARTING, IN_GAME, ENDED;

        @Override
        public String toString() {
            String name = this.name();
            name = name.replace("_", " ");
            return WordUtils.capitalizeFully(name);
        }
    }
}
