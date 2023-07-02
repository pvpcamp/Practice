package camp.pvp.events.games;

import camp.pvp.games.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpectateStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private Player player, target;
    public SpectateStartEvent(Game game, Player player, Player target) {
        this.game = game;
        this.player = player;
        this.target = target;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
