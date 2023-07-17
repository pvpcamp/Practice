package camp.pvp.practice.interactables.impl.party;

import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
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
