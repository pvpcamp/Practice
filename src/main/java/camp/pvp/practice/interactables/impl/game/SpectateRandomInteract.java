package camp.pvp.practice.interactables.impl.game;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpectateRandomInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Game fromGame = gameProfile.getGame();
        Game toGame = null;

        List<Game> games = new ArrayList<>(Practice.getInstance().getGameManager().getActiveGames());
        Collections.shuffle(games);

        for(Game game : games) {
            if(game != fromGame) {
                toGame = game;
                break;
            }
        }

        if(toGame != null) {
            fromGame.spectateEnd(player, false);
            toGame.spectateStartRandom(player);
        } else {
            player.sendMessage(ChatColor.RED + "There are no other games to spectate.");
        }
    }
}
