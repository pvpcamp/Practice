package camp.pvp.practice.queue;

import camp.pvp.practice.Practice;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Date;
import java.util.UUID;

@Getter @Setter
public class GameQueueMember {

    private final UUID uuid;
    private final String name;
    private final Date joined;
    private int eloLow, eloHigh, elo;
    private GameQueue queue;
    private BukkitTask queueUpdater;

    public GameQueueMember(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.joined = new Date();
    }

    public GameQueueMember(UUID uuid, String name, int elo) {
        this.uuid = uuid;
        this.name = name;
        this.joined = new Date();
        this.elo = elo;
        this.eloLow = elo;
        this.eloHigh = elo;

        this.queueUpdater = Bukkit.getScheduler().runTaskTimerAsynchronously(Practice.instance, new Runnable() {
            @Override
            public void run() {
                eloLow = Math.max(eloLow - 20, 0);
                eloHigh = Math.min(eloHigh + 20, 2000);
            }
        }, 0, 60);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
