package camp.pvp.practice.profiles;

import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.queue.GameQueue;

public record PreviousQueue(GameKit kit, GameQueue.Type queueType) {
}
