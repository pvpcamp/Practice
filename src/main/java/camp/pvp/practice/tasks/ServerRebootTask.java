package camp.pvp.practice.tasks;

import camp.pvp.practice.Practice;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class ServerRebootTask implements Runnable{

    private Practice plugin;
    private Date rebootTime;
    private int timeUntilRestart;
    public ServerRebootTask(Practice plugin) {
        this.plugin = plugin;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 3);

        rebootTime = calendar.getTime();
        timeUntilRestart = -1;
    }

    @Override
    public void run() {
        if(rebootTime.before(new Date())) {
            if(timeUntilRestart == -1) {
                timeUntilRestart = 300;
            }

            List<Integer> thresholds = Arrays.asList(300, 240, 180, 120, 60, 45, 30, 15, 10, 5, 4, 3, 2, 1);
            if(thresholds.contains(timeUntilRestart)) {
                long minutes = TimeUnit.SECONDS.toMinutes(timeUntilRestart) % 60;
                if(minutes == 0) {
                    Bukkit.broadcastMessage(Colors.get("&c&l[REBOOT] &f&lThis server will restart in " + timeUntilRestart + " second(s)."));
                } else {
                    Bukkit.broadcastMessage(Colors.get("&c[REBOOT] &fThis server will restart in " + minutes + " minutes(s)."));
                }
            }

            if(timeUntilRestart == 60) {
                Bukkit.broadcastMessage(Colors.get("&4&lActive ranked matches will be cancelled with no ELO change when the server restarts."));
            }

            if(timeUntilRestart == 0) {
                plugin.shutdown();
                Bukkit.getScheduler().runTaskLater(plugin, ()-> plugin.getServer().shutdown(), 20);
            }

            timeUntilRestart--;
        }
    }
}
