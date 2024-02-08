package camp.pvp.practice.interactables.impl.lobby;

import camp.pvp.practice.guis.profile.cosmetics.CosmeticsGui;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class CosmeticsInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        new CosmeticsGui(gameProfile).open(player);
    }
}
