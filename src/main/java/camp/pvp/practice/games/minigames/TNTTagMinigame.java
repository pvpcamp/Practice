package camp.pvp.practice.games.minigames;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.profiles.GameProfile;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class TNTTagMinigame extends Minigame {

    @Getter @Setter private int round, timer;
    private BukkitTask roundTimerTask;

    public TNTTagMinigame(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public GameParticipant createParticipant(Player player) {
        return new TNTTagParticipant(player.getUniqueId(), player.getName());
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        return null;
    }

    @Override
    public List<String> getSpectatorScoreboard(GameProfile profile) {
        return null;
    }

    @Override
    public void initialize() {

    }

    public void nextRound() {

    }

    public TNTTagParticipant getTagged() {
        Collection<GameParticipant> participants = getParticipants().values();
        for(GameParticipant participant : participants) {
            TNTTagParticipant tntTagParticipant = (TNTTagParticipant) participant;
            if(tntTagParticipant.isTagged()) {
                return tntTagParticipant;
            }
        }

        return null;
    }

    @Override
    public GameParticipant determineWinner() {
        return null;
    }
}
