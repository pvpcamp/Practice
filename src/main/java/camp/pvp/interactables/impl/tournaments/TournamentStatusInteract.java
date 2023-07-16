package camp.pvp.interactables.impl.tournaments;

import camp.pvp.games.tournaments.Tournament;
import camp.pvp.interactables.ItemInteract;
import camp.pvp.profiles.GameProfile;
import org.bukkit.entity.Player;

public class TournamentStatusInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        player.performCommand("tournament status");
    }
}
