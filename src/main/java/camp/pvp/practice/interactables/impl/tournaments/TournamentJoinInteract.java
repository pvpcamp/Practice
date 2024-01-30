package camp.pvp.practice.interactables.impl.tournaments;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.tournaments.Tournament;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class TournamentJoinInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Tournament tournament = Practice.getInstance().getGameManager().getTournament();
        tournament.join(player);
    }
}
