package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Queue;

@Getter @Setter
public class ArenaCopyQueue implements Runnable{

    private final Practice plugin;
    private final Player player;
    private int taskId;
    private Queue<ArenaCopyTask> copyQueue;
    public ArenaCopyQueue(Practice plugin, Player player, Queue<ArenaCopyTask> copyQueue) {
        this.plugin = plugin;
        this.player = player;
        this.copyQueue = copyQueue;
    }

    @Override
    public void run() {
        if(copyQueue.isEmpty()) {
            player.sendMessage(ChatColor.GREEN + "The arena copy queue has completed.");
            Bukkit.getScheduler().cancelTask(taskId);
            return;
        }

        final ArenaCopyTask act = copyQueue.peek();
        if(act.getStarted() == 0) {
            int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, act, 0, 2);
            act.setTaskId(id);
        }

        if(act.getEnded() > 0) {
            Bukkit.getScheduler().cancelTask(act.getTaskId());
            copyQueue.poll();
        }
    }
}
