package camp.pvp.practice.interactables.impl.lobby;

import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.DuelRequest;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.Rematch;
import org.bukkit.entity.Player;

public class RematchInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Rematch rematch = gameProfile.getRematch();
        DuelRequest duelRequest = gameProfile.getDuelRequests().get(rematch.getUuid());
        if(duelRequest != null && !duelRequest.isExpired() && duelRequest.getKit().equals(rematch.getKit())) {
            duelRequest.startGame();
        } else {
            gameProfile.getRematch().send();
        }
    }
}
