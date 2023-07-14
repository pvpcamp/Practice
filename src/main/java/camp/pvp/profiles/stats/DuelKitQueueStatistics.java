package camp.pvp.profiles.stats;

import camp.pvp.kits.DuelKit;
import camp.pvp.queue.GameQueue;
import camp.pvp.utils.ItemUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class DuelKitQueueStatistics {

    private final DuelKit kit;
    private final GameQueue.Type queueType;
    private int wins, losses, winStreak;

    public DuelKitQueueStatistics(DuelKit kit, GameQueue.Type queueType) {
        this.kit = kit;
        this.queueType = queueType;
    }

    public Map<String, Object> exportItems() {
        Map<String, Object> map = new HashMap<>();
        map.put("wins", wins);
        map.put("losses", losses);
        map.put("win_streak", winStreak);

        return map;
    }

    public void importFromMap(Map<String, Object> map) {
        this.wins = (int) map.get("wins");
        this.losses = (int) map.get("losses");
        this.winStreak = (int) map.get("win_streak");
    }
}
