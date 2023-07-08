package camp.pvp.interactables.impl.game;

import camp.pvp.games.Game;
import camp.pvp.interactables.ItemInteract;
import camp.pvp.profiles.GameProfile;
import org.bukkit.entity.Player;

public class StopSpectatingInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Game game = gameProfile.getGame();
        game.spectateEnd(player);
    }
}
