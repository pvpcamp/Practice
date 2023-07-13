package camp.pvp.listeners.bukkit.player;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.Colors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveListeners implements Listener {

    private Practice plugin;
    public PlayerJoinLeaveListeners(Practice plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        plugin.getGameProfileManager().find(event.getUniqueId(), true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GameProfile profile = plugin.getGameProfileManager().find(player.getUniqueId(), true);

        if(profile == null) {
            profile = plugin.getGameProfileManager().create(player);
        }

        profile.setName(player.getName());
        profile.playerUpdate();

        plugin.getGameProfileManager().updateGlobalPlayerVisibility();

        event.setJoinMessage(null);

        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append("\n&6Welcome to &6&lPvP Camp&r&6!");
        sb.append("\n&7&oWe are currently in development, please report any bugs to the developers.");
        sb.append("\n ");
        player.sendMessage(Colors.get(sb.toString()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        Game game = profile.getGame();

        if(game != null) {
            switch(profile.getState()) {
                case SPECTATING:
                    game.spectateEnd(player);
                    break;
                case IN_GAME:
                    game.eliminate(player, true);
                    break;
            }
        }

        if(profile.getParty() != null) {
            profile.getParty().leave(player);
        }

        plugin.getGameProfileManager().exportToDatabase(player.getUniqueId(), true, false);

        event.setQuitMessage(null);
    }
}
