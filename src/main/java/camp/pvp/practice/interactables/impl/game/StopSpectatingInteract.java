package camp.pvp.practice.interactables.impl.game;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.interactables.ItemInteract;
import org.bukkit.entity.Player;

public class StopSpectatingInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Game game = gameProfile.getGame();
        game.spectateEnd(player);
    }
}
