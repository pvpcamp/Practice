package camp.pvp.practice.games.minigames;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.tasks.TeleportFix;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class OneInTheChamberMinigame extends QueueableMinigame{
    public OneInTheChamberMinigame(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public void initialize() {
        if(getArena() == null) {
            setArena(getPlugin().getArenaManager().selectRandomArena(getKit()));
        }

        if(getArena() == null) {
            for(Player p : getAlivePlayers()) {
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(p.getUniqueId());
                p.sendMessage(ChatColor.RED + "There are no arenas currently available for the ladder selected. Please notify a staff member.");
                profile.setGame(null);
                profile.playerUpdate(true);
            }
            return;
        }

        Arena arena = getArena();

        arena.prepare();

        if(!arena.hasValidPositions()) {
            for(Player p : getAlivePlayers()) {
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(p.getUniqueId());
                p.sendMessage(ChatColor.RED + "The arena " + arena.getName() + " does not have valid spawn points, please notify a staff member.");
                profile.setGame(null);
                profile.playerUpdate(true);
            }
            return;
        }

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
                 &7● &6Minigame: &fOne in the Chamber
                 &7● &6Arena: &f%s
                 &7● &6Participants: &f%s
                """.formatted(arena.getDisplayName(), sb);

        int spawnNumber = 1;
        for(GameParticipant participant : getParticipants().values()) {
            ArenaPosition pos = arena.getPositions().get("spawn" + spawnNumber);
            Player p = Bukkit.getPlayer(participant.getUuid());
            p.teleport(pos.getLocation());
            p.sendMessage(startingMessage);
        }

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

        Bukkit.getScheduler().runTaskLater(getPlugin(), new TeleportFix(this), 1);

        startingTimer(5);
    }

    @Override
    public void end() {

    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();

        GameParticipant self = this.getParticipants().get(profile.getUuid());

        switch(this.getState()) {
            case STARTING -> {
                lines.add("&6Minigame: &fOITC");
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                lines.add(" ");

                lines.add("&6Players:");
                for(GameParticipant p : this.getCurrentPlaying().values()) {
                    if(p.getUuid() != profile.getUuid()) lines.add("&f" + p.getName());
                }
            }
            case ACTIVE -> {

                boolean addSpace = false;
                if(profile.isSidebarShowPing()) {
                    addSpace = true;
                    lines.add("&6Ping: &f" + PlayerUtils.getPing(profile.getPlayer()));
                }

                if(profile.isSidebarShowDuration()) {
                    addSpace = true;
                    lines.add("&6Duration: &f" + TimeUtil.get(getStarted()));
                }

                if(addSpace) lines.add(" ");

                lines.add("&6Kills:");

                List<GameParticipant> sp = new ArrayList<>(this.getCurrentPlaying().values());
                sp.sort((p1, p2) -> Integer.compare(p2.getKills(), p1.getKills()));

                for(GameParticipant p : this.getCurrentPlaying().values()) {
                    lines.add(p.getName() + " &7" + p.getKills());
                }
            }
            case ENDED -> {
                GameParticipant winner = null;
                for(GameParticipant p : getCurrentPlaying().values()) {
                    if(p.getKills() == 20) {
                        winner = p;
                        break;
                    }
                }

                if(winner != null) lines.add("&6Winner: &f" + winner.getName());

                if(profile.isSidebarShowDuration()) {
                    lines.add("&6Duration: &f&n" + TimeUtil.get(getEnded(), getStarted()));
                }
            }
        }

        return lines;
    }
}
