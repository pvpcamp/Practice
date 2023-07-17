package camp.pvp.practice.interactables.impl.tournaments;

import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class TournamentStatusInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        player.performCommand("tournament status");
    }
}
