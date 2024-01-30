package camp.pvp.practice.interactables.impl.game;

import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class ShowSpectatorsInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        gameProfile.setSpectatorVisibility(!gameProfile.isSpectatorVisibility());
        gameProfile.givePlayerItems(false);
        gameProfile.updatePlayerVisibility();
    }
}
