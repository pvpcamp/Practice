package camp.pvp.practice.games.tasks;

import camp.pvp.practice.games.Game;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;

public class StartingTask extends BukkitRunnable {

    private Game game;
    private int time;
    public StartingTask(Game game, int time) {
        this.game = game;
        this.time = time;
    }


    @Override
    public void run() {
        if (time == 0) {
            for(Player p : game.getAllPlayers()) {
                if(p != null) {
                    p.removePotionEffect(PotionEffectType.JUMP);
                    p.playSound(p.getLocation(), Sound.FIREWORK_BLAST, 1, 1);
                    p.sendMessage(ChatColor.GREEN + "The game has started.");
                }
            }

            game.setStarted(new Date());
            game.setState(Game.State.ACTIVE);

            this.cancel();
        } else {
            if (time > 0) {
                for (Player p : game.getAllPlayers()) {
                    p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                    p.sendMessage(ChatColor.GREEN.toString() + time + "...");
                }
            }

            time -= 1;
        }
    }
}
