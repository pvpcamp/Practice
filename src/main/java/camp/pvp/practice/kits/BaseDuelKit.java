package camp.pvp.practice.kits;

import camp.pvp.practice.arenas.Arena;

public abstract class BaseDuelKit extends BaseKit {
    public BaseDuelKit(NewGameKit gameKit) {
        super(gameKit);

        getArenaTypes().add(Arena.Type.DUEL);
        getArenaTypes().add(Arena.Type.DUEL_FLAT);

        setTournament(true);
        setTeams(true);
        setRanked(true);
    }
}
