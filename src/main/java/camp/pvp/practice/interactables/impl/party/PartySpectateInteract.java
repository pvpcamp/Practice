package camp.pvp.practice.interactables.impl.party;

import camp.pvp.practice.parties.Party;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class PartySpectateInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Party party = gameProfile.getParty();
        if(party.getGame() != null) {
            party.getGame().spectateStartRandom(player);
        }
    }
}
