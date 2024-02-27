package camp.pvp.practice.games.minigames;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.GameParticipant;

import java.util.UUID;

public class SkywarsMinigame extends QueueableMinigame{

    public SkywarsMinigame(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public GameParticipant determineWinner() {
        return null;
    }

    @Override
    public GameParticipant getWinner() {
        return null;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void end() {

    }
}
