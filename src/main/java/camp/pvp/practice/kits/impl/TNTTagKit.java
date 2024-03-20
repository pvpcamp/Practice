package camp.pvp.practice.kits.impl;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.kits.BaseKit;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.queue.GameQueue;

import java.util.List;

public class TNTTagKit extends BaseKit {

    public TNTTagKit() {
        super(GameKit.TNT_TAG);

        getGameTypes().add(GameQueue.GameType.MINIGAME);
        setArenaTypes(List.of(Arena.Type.MINIGAME_TNT_TAG));

        setTakeDamage(false);
        setFallDamage(false);
        setEditable(false);
        setDropItemsOnDeath(false);
        setHunger(false);
    }
}
