package camp.pvp.practice.games.minigames;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.tasks.TeleportFix;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class SkywarsMinigame extends Minigame {

    public SkywarsMinigame(Practice plugin, UUID uuid) {
        super(plugin, uuid);

        setType(Type.SKYWARS);
        setKit(GameKit.SKYWARS.getBaseKit());
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        GameParticipant self = getParticipants().get(profile.getUuid());
        List<String> lines = new ArrayList<>();

        switch(getState()) {
            case STARTING:
                lines.add("&6Minigame: &fSkywars");
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                lines.add("&6Players: &f" + this.getCurrentPlaying().size());
                break;
            case ACTIVE:
                lines.add("&6Players Alive: &f" + this.getCurrentPlaying().size() + "/" + this.getParticipants().size());
                lines.add("&6Kills: &f" + self.getKills());

                if(profile.isSidebarShowPing()) {
                    lines.add("&6Ping: &f" + PlayerUtils.getPing(profile.getPlayer()) + " ms");
                }

                if(profile.isSidebarShowDuration()) {
                    lines.add("&6Duration: &f" + TimeUtil.get(getStarted()));
                }
                break;
            case ENDED:
                lines.add("&6Winner: &f" + getWinner().getName());
                break;
        }

        return lines;
    }

    @Override
    public List<String> getSpectatorScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();

        switch(getState()) {
            case STARTING:
                lines.add("&6Minigame: &fSkywars");
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                lines.add("&6Players: &f" + this.getCurrentPlaying().size());
                break;
            case ACTIVE:
                lines.add("&6Players Alive: &f" + this.getCurrentPlaying().size() + "/" + this.getParticipants().size());

                if(profile.isSidebarShowDuration()) {
                    lines.add(" ");
                    lines.add("&6Duration: &f" + TimeUtil.get(getStarted()));
                }
                break;
            case ENDED:
                lines.add("&6Winner: &f" + getWinner().getName());
                break;
        }

        return lines;
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

        sendStartingMessage();

        int i = 1;
        for(GameParticipant participant : getParticipants().values()) {
            Player p = Bukkit.getPlayer(participant.getUuid());

            ArenaPosition position = arena.getPositions().get("spawn" + i);
            participant.setSpawnLocation(position.getLocation());

            p.teleport(position.getLocation());
            participant.getProfile().givePlayerItems();

            i++;
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
    public GameParticipant createParticipant(Player player) {
        return new GameParticipant(player.getUniqueId(), player.getName());
    }
}
