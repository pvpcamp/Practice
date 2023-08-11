package camp.pvp.practice.arenas;

import camp.pvp.practice.Practice;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Queue;

@Getter @Setter
public class ArenaCopyQueue implements Runnable{

    private final Practice plugin;
    private int taskId;
    private Queue<ArenaCopier> copyQueue;
    public ArenaCopyQueue(Practice plugin) {
        this.plugin = plugin;
        this.copyQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        if(!copyQueue.isEmpty()) {
            final ArenaCopier act = copyQueue.peek();
            if(act.getStarted() == 0) {
                int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, act, 0, 1);
                act.setTaskId(id);
            }

            if(act.getEnded() > 0) {
                Bukkit.getScheduler().cancelTask(act.getTaskId());
                copyQueue.poll();
            }
        }
    }
}
