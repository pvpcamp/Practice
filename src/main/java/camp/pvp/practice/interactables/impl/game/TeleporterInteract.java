package camp.pvp.practice.interactables.impl.game;

import camp.pvp.practice.guis.games.SpectatorGui;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class TeleporterInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        new SpectatorGui(gameProfile.getGame()).open(player);
    }
}
