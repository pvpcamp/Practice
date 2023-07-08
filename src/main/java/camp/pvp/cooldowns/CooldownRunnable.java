package camp.pvp.cooldowns;

import camp.pvp.Practice;
import camp.pvp.games.GameManager;
import camp.pvp.games.Game;
import camp.pvp.games.GameParticipant;

public class CooldownRunnable implements Runnable{

    private Practice plugin;
    private GameManager gameManager;
    public CooldownRunnable(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for(Game game : gameManager.getActiveGames()) {
            for(GameParticipant participant : game.getParticipants().values()) {
                for(PlayerCooldown cooldown : participant.getCooldowns().values()) {
                    cooldown.check();
                }
            }
        }
    }
}
