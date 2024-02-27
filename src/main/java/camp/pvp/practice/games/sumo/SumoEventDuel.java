package camp.pvp.practice.games.sumo;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.impl.Duel;
import camp.pvp.practice.games.tasks.TeleportFix;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.GameProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;

public class SumoEventDuel extends Duel {

    private final SumoEvent sumoEvent;

    public SumoEventDuel(Practice plugin, SumoEvent event) {
        super(plugin, UUID.randomUUID());
        this.sumoEvent = event;
        this.setArena(event.getArena());
        this.setKit(GameKit.SUMO);
    }

    @Override
    public void initialize() {
        Map<Player, Location> locations = new HashMap<>();

        this.setState(State.STARTING);

        int position = 1;
        for (Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
            GameParticipant participant = entry.getValue();
            Player p = Bukkit.getPlayer(entry.getKey());
            Location location = null;
            if (position == 1) {
                location = getArena().getPositions().get("spawn1").getLocation();
            }

            if (position == 2) {
                location = getArena().getPositions().get("spawn2").getLocation();
            }

            if (p != null) {
                locations.put(p, location);
                p.teleport(locations.get(p));
                participant.setSpawnLocation(location);
                participant.getProfile().givePlayerItems();
            }
            position++;
        }

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

        Bukkit.getScheduler().runTaskLater(getPlugin(), new TeleportFix(this), 1);

        startingTimer(3);
    }

    @Override
    public void end() {
        GameProfileManager gpm = getPlugin().getGameProfileManager();
        setEnded(new Date());
        setState(State.ENDED);

        if(getStarted() == null) {
            setStarted(new Date());
            getStartingTimer().cancel();
        }

        GameParticipant winnerParticipant = null, loserParticipant = null;
        GameProfile winnerProfile = null, loserProfile = null;

        for(Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
            GameParticipant participant = entry.getValue();

            boolean alive = participant.isCurrentlyPlaying();
            if(alive) {
                winnerParticipant = participant;
                winnerProfile = gpm.getLoadedProfiles().get(entry.getKey());
            } else {
                loserParticipant = participant;
                loserProfile = gpm.getLoadedProfiles().get(entry.getKey());
            }
        }

        clearEntities();
        for(GameParticipant participant : getCurrentPlaying().values()) {
            participant.getPlayer().teleport(getArena().getPositions().get("lobby").getLocation());
            participant.getProfile().givePlayerItems(false);
            participant.getProfile().setGame(null);
        }
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        return sumoEvent.getScoreboard(profile);
    }

    @Override
    public void eliminate(Player player, boolean leftGame) {
        GameParticipant participant = getParticipants().get(player.getUniqueId());

        if(participant == null || !participant.isAlive()) return;

        if(this.getState().equals(State.ENDED)) return;

        participant.setLivingState(GameParticipant.LivingState.DEAD);

        GameProfile profile = participant.getProfile();
        Location location = player.getLocation();

        boolean velocity = participant.getLastDamageCause() != null && participant.getLastDamageCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        profile.getDeathAnimation().playAnimation(this, player, location, velocity);

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

        end();

        sumoEvent.eliminate(player, leftGame);
    }

    @Override
    public void spectateStart(Player player, Location location) {
        sumoEvent.join(player);
    }

    @Override
    public List<Player> getAllPlayers() {
        return sumoEvent.getActivePlayers();
    }

    @Override
    public boolean seeEveryone() {
        return true;
    }
}
