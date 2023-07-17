package camp.pvp.practice.cooldowns;

import camp.pvp.practice.games.GameManager;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;

public class CooldownRunnable implements Runnable{

    private Practice plugin;
    private GameManager gameManager;
    public CooldownRunnable(Practice plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
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
