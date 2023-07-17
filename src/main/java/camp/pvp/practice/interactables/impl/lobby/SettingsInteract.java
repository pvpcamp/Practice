package camp.pvp.practice.interactables.impl.lobby;

import camp.pvp.practice.guis.profile.SettingsGui;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class SettingsInteract implements ItemInteract {

    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        SettingsGui gui = new SettingsGui(gameProfile);
        gui.open(player);
    }
}
