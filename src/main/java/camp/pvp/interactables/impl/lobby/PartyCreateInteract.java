package camp.pvp.interactables.impl.lobby;

import camp.pvp.Practice;
import camp.pvp.interactables.ItemInteract;
import camp.pvp.parties.Party;
import camp.pvp.profiles.GameProfile;
import org.bukkit.entity.Player;

public class PartyCreateInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        if(gameProfile.getState().equals(GameProfile.State.LOBBY)) {
            Practice plugin = Practice.instance;
            Party party = new Party(plugin);
            plugin.getPartyManager().getParties().add(party);
            party.join(player);
        }
    }
}
