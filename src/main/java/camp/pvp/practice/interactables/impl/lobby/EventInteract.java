package camp.pvp.practice.interactables.impl.lobby;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.sumo.SumoEvent;
import camp.pvp.practice.interactables.ItemInteract;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.entity.Player;

public class EventInteract implements ItemInteract
{
    @Override
    public void onInteract(Player player, GameProfile gameProfile) {
        SumoEvent activeEvent = Practice.getInstance().getGameManager().getActiveEvent();

        if(activeEvent == null) {
            player.performCommand("event host");
        } else {
            activeEvent.join(player);
        }
    }
}
