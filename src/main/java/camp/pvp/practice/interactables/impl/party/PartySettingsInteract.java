package camp.pvp.practice.interactables.impl.party;

import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class PartySettingsInteract implements ItemInteract {
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        player.performCommand("party settings");
    }
}
