package camp.pvp.practice.games.sumo;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.impl.Duel;
import camp.pvp.practice.games.impl.events.SumoEvent;

import java.util.UUID;

public class SumoEventDuel extends Duel {

    private SumoEvent sumoEvent;

    public SumoEventDuel(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }
}
