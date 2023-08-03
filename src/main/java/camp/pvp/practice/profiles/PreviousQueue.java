package camp.pvp.practice.profiles;

import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.queue.GameQueue;
import lombok.Getter;

@Getter
public class PreviousQueue {

    private final DuelKit kit;
    private final GameQueue.Type queueType;


    public PreviousQueue(DuelKit kit, GameQueue.Type queueType) {
        this.kit = kit;
        this.queueType = queueType;
    }
}
