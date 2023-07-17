package camp.pvp.practice.interactables.impl.party;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.interactables.ItemInteract;
import org.bukkit.entity.Player;

public class PartyLeaveInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        gameProfile.getParty().leave(player);
    }
}
