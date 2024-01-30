package camp.pvp.practice.interactables.impl.game;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameSpectator;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class VisibleToPlayersInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Game game = gameProfile.getGame();
        if(game != null && game.getSpectators().containsKey(player.getUniqueId())) {
            GameSpectator spectator = game.getSpectators().get(player.getUniqueId());
            spectator.setVisibleToPlayers(!spectator.isVisibleToPlayers());
            gameProfile.givePlayerItems(false);
            Practice.getInstance().getGameProfileManager().updateGlobalPlayerVisibility();
        }
    }
}
