package camp.pvp.practice.interactables.impl.lobby;

import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HostEventInteract implements ItemInteract
{
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        player.sendMessage(ChatColor.GREEN + "Coming soon!");
    }
}
