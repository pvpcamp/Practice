package camp.pvp.practice.games.minigames;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.tasks.TeleportFix;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.*;

public class OneInTheChamberMinigame extends QueueableMinigame{

    public OneInTheChamberMinigame(Practice plugin, UUID uuid) {
        super(plugin, uuid);

        setKit(GameKit.ONE_IN_THE_CHAMBER);
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
                 &7● &6Minigame: &fOne In The Chamber
                 &7● &6Arena: &f%s
                 &7● &6Participants: &f%s
                 
                 """.formatted(arena.getDisplayName(), sb);

        for(GameParticipant participant : getParticipants().values()) {
            Player p = Bukkit.getPlayer(participant.getUuid());
            p.teleport(getRespawnLocation(participant));
            p.sendMessage(startingMessage);
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
    public void handleHit(Player victim, Player attacker, EntityDamageByEntityEvent event) {
        super.handleHit(victim, attacker, event);

        GameParticipant victimParticipant = getParticipants().get(victim.getUniqueId());
        GameParticipant attackerParticipant = getParticipants().get(attacker.getUniqueId());

        if(!victimParticipant.isAlive()) return;

        if(event.getDamager() instanceof Arrow) {
            eliminate(victim.getPlayer(), false);
        }

        if(attackerParticipant.getKills() > 19) {
            end();
        }
    }

    @Override
    public void end() {
        GameParticipant winner = determineWinner();

        GameProfileManager gpm = getPlugin().getGameProfileManager();
        setEnded(new Date());
        setState(State.ENDED);

        if(getStarted() == null) {
            setStarted(new Date());
            getStartingTimer().cancel();
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Colors.get(" \n&6&lMatch ended."));
        stringBuilder.append("\n &7● &6Winner: &f" + winner.getName());
        stringBuilder.append(" \n");
        stringBuilder.append("\n&6&lKills:");

        List<GameParticipant> sortedParticipants = new ArrayList<>(this.getParticipants().values());
        sortedParticipants.sort((p1, p2) -> Integer.compare(p2.getKills(), p1.getKills()));

        for(GameParticipant p : sortedParticipants) {
            stringBuilder.append("\n &f" + p.getName() + " &7- &f" + p.getKills() + " Kill(s)");
        }

        StringBuilder sortedNames = new StringBuilder();
        List<Player> players = new ArrayList<>(this.getAlivePlayers());
        players.sort(Comparator.comparing(HumanEntity::getName));

        int s = 0;
        while(s != this.getAlive().size()) {
            Player p = players.get(0);
            sortedNames.append(ChatColor.WHITE + p.getName());

            players.remove(p);
            s++;
            if(s == this.getAlivePlayers().size()) {
                sortedNames.append(ChatColor.GRAY + ".");
            } else {
                sortedNames.append(ChatColor.GRAY + ", ");
            }
        }

        sortedNames.append(" ");

        for(Player player : this.getAllPlayers()) {
            player.sendMessage(sortedNames.toString());
        }

        cleanup(3);
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();

        GameParticipant self = this.getParticipants().get(profile.getUuid());

        switch(this.getState()) {
            case STARTING -> {
                lines.add("&6Minigame: &fOITC");
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                lines.add("&7&oFirst to 20 kills wins!");

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

                for(GameParticipant p : sp) {
                    lines.add(p.getName() + " &7" + p.getKills());
                }
            }
            case ENDED -> {
                lines.add("&6Winner: &f" + getWinner().getName());

                if(profile.isSidebarShowDuration()) {
                    lines.add("&6Duration: &f&n" + TimeUtil.get(getEnded(), getStarted()));
                }
            }
        }

        return lines;
    }

    @Override
    public GameParticipant determineWinner() {

        List<GameParticipant> sp = new ArrayList<>(this.getCurrentPlaying().values());
        sp.sort((p1, p2) -> Integer.compare(p2.getKills(), p1.getKills()));
        setWinner(sp.get(0));

        return getWinner();
    }
}
