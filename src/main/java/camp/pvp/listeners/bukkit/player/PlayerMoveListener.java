package camp.pvp.listeners.bukkit.player;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.profiles.GameProfile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private Practice plugin;
    public PlayerMoveListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        Game game = profile.getGame();

        Location from = event.getFrom();
        Location to = event.getTo();

        if(game != null) {
            if(event.getTo().getBlock().isLiquid() && game.getCurrentPlaying().contains(player)) {
                if(game.getKit().isDieInWater() && game.getState().equals(Game.State.ACTIVE)) {
                    game.eliminate(player, false);
                }
            }

            if(!game.getKit().isMoveOnStart()) {
                if(game.getState().equals(Game.State.STARTING) && game.getCurrentPlaying().contains(player)) {
                    if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                        player.teleport(from);
                    }
                }
            }
        }
    }
}
