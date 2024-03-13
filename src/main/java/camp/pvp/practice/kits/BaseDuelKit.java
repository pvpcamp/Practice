package camp.pvp.practice.kits;

import camp.pvp.practice.queue.GameQueue;

public abstract class BaseDuelKit extends BaseKit {
    public BaseDuelKit(GameKit gameKit) {
        super(gameKit);

        getGameTypes().add(GameQueue.GameType.DUEL);

        setTournament(true);
        setTeams(true);
        setRanked(true);
    }
}
