package camp.pvp.practice.kits.impl;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.kits.BaseKit;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.queue.GameQueue;

public class TNTTagKit extends BaseKit {

    public TNTTagKit(GameKit gameKit) {
        super(gameKit);

        getGameTypes().add(GameQueue.GameType.MINIGAME);
        getArenaTypes().add(Arena.Type.MINIGAME_TNT_TAG);

        setTakeDamage(false);
        setFallDamage(false);
        setEditable(false);
        setDropItemsOnDeath(false);
        setHunger(false);
    }
}
