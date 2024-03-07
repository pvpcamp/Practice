package camp.pvp.practice.games.minigames;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.GameParticipant;

import java.util.UUID;

public class FireballBlitzMinigame extends Minigame{

    protected FireballBlitzMinigame(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void end() {

    }

    @Override
    public GameParticipant determineWinner() {
        return null;
    }
}
