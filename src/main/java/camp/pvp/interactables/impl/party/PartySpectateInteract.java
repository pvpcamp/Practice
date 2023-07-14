package camp.pvp.interactables.impl.party;

import camp.pvp.interactables.ItemInteract;
import camp.pvp.parties.Party;
import camp.pvp.profiles.GameProfile;
import org.bukkit.entity.Player;

public class PartySpectateInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Party party = gameProfile.getParty();
        if(party.getGame() != null) {
            party.getGame().spectateStart(player, null);
        }
    }
}
