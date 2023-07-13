package camp.pvp.interactables.impl.party;

import camp.pvp.interactables.ItemInteract;
import camp.pvp.parties.Party;
import camp.pvp.profiles.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PartyTeamsInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        Party party = gameProfile.getParty();
        player.sendMessage(ChatColor.GREEN + "Coming soon!");
    }
}
