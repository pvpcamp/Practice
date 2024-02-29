package camp.pvp.practice.games.minigames;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.tasks.TeleportFix;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class SkywarsMinigame extends Minigame {

    public SkywarsMinigame(Practice plugin, UUID uuid) {
        super(plugin, uuid);

        setType(Type.SKYWARS);
        setKit(GameKit.SKYWARS);
    }

    @Override
    public GameParticipant determineWinner() {
        GameParticipant winner = getCurrentPlaying().values().stream().findFirst().orElse(null);
        setWinner(winner);
        return winner;
    }

    @Override
    public void initialize() {
        if(getArena() == null) {
            setArena(getPlugin().getArenaManager().selectRandomArena(Arena.Type.MINIGAME_SKYWARS));
        }

        if(getArena() == null) {
            for(Player p : getAlivePlayers()) {
                p.sendMessage(ChatColor.RED + "There are no arenas currently available for the ladder selected. Please notify a staff member.");
                cleanup(0);
            }
            return;
        }

        Arena arena = getArena();

        arena.prepare();

        this.setState(State.STARTING);

        List<Player> players = new ArrayList<>(this.getAlivePlayers());
        players.sort(Comparator.comparing(HumanEntity::getName));

        StringBuilder sb = new StringBuilder();

        int s = 0;
        while(s != this.getAlive().size()) {
            Player p = players.get(0);
            sb.append(ChatColor.WHITE + p.getName());

            players.remove(p);
            s++;
            if(s == this.getAlivePlayers().size()) {
                sb.append(ChatColor.GRAY + ".");
            } else {
                sb.append(ChatColor.GRAY + ", ");
            }
        }

        String startingMessage = """
                
                &6&lMinigame starting in 5 seconds.
                 &7● &6Minigame: &fSkywars
                 &7● &6Arena: &f%s
                 &7● &6Participants: &f%s
                 \n
                 """.formatted(arena.getDisplayName(), sb);

        int i = 1;
        for(GameParticipant participant : getParticipants().values()) {
            Player p = Bukkit.getPlayer(participant.getUuid());

            ArenaPosition position = arena.getPositions().get("spawn" + i);
            participant.setSpawnLocation(position.getLocation());

            p.teleport(position.getLocation());
            p.sendMessage(Colors.get(startingMessage));
            participant.getProfile().givePlayerItems();
        }

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

        Bukkit.getScheduler().runTaskLater(getPlugin(), new TeleportFix(this), 1);

        startingTimer(5);
    }

    @Override
    public void eliminate(Player player, boolean leftGame) {
        super.eliminate(player, leftGame);

        if(getCurrentPlaying().size() < 2) {
            end();
        }
    }

    @Override
    public void end() {

        if(getState() == State.ENDED) return;

        GameParticipant winner = determineWinner();

        setEnded(new Date());
        setState(State.ENDED);

        if(getStarted() == null) {
            setStarted(new Date());
            getStartingTimer().cancel();
        }

        StringBuilder topPlayers = new StringBuilder();
        List<GameParticipant> sortedParticipants = new ArrayList<>(this.getParticipants().values());
        sortedParticipants.sort((p1, p2) -> Integer.compare(p2.getKills(), p1.getKills()));

        for(int i = 0; i < Math.min(sortedParticipants.size(), 3); i++) {
            GameParticipant p = sortedParticipants.get(i);
            topPlayers.append("\n &7● &6" + (i + 1) + ": &f" + p.getName() + " &7- &f" + p.getKills() + " Kill" + (p.getKills() == 1 ? "" : "s"));
        }

        String endMessage = """
                
                &6&lMinigame finished.
                 &7● &6Winner: &f%s
                 \n
                &6&lTop Players: %s
                 \n
                """.formatted(winner.getName(), topPlayers.toString());

        for(Player player : this.getAllPlayers()) {
            player.sendMessage(Colors.get(endMessage));
        }

        cleanup(3);
    }
}
