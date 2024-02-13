package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Arrays;

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

        if(profile != null) {
            Game game = profile.getGame();

            Location from = event.getFrom();
            Location to = event.getTo();

            if (game != null) {
                GameParticipant participant = game.getParticipants().get(player.getUniqueId());
                if(game.getAlivePlayers().contains(player)) {
                    if (event.getTo().getBlock().isLiquid()) {
                        if (game.getKit().isDieInWater() && game.getState().equals(Game.State.ACTIVE)) {
                            participant.setLastDamageCause(EntityDamageEvent.DamageCause.DROWNING);
                            game.eliminate(player, false);
                        }
                    }

                    if (!game.getKit().isMoveOnStart()) {
                        if (!Arrays.asList(Game.State.ACTIVE, Game.State.ENDED).contains(game.getState()) && game.getCurrentPlayersPlaying().contains(player)) {
                            if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                                player.teleport(from);
                            }
                        }
                    }

                    if(event.getTo().getY() < 0 || event.getTo().getY() < game.getArena().getVoidLevel()) {
                        if(game.getState().equals(Game.State.ACTIVE)) {
                            participant.setLastDamageCause(EntityDamageEvent.DamageCause.VOID);
                            game.eliminate(player, false);
                        }
                    }

                    game.handleBorder(player);
                } else {
                    if(event.getTo().getY() < 0) {
                        player.teleport(game.getAllPlayers().get(0));
                    }
                }
            }
        }
    }
}
