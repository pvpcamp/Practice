package camp.pvp.listeners.bukkit.player;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.kits.DuelKit;
import camp.pvp.profiles.GameProfile;
import camp.pvp.queue.GameQueue;
import camp.pvp.utils.Colors;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketModSettings;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketNotification;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketServerRule;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import com.lunarclient.bukkitapi.object.StaffModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
        GameProfile profile = plugin.getGameProfileManager().find(event.getUniqueId(), true);
    }

    @EventHandler(priority = EventPriority.HIGH)
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

        LunarClientAPI lcApi = LunarClientAPI.getInstance();

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                GameProfile p = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
                if (player.isOnline()) {
                    if (p == null) {
                        player.kickPlayer(ChatColor.RED + "There was an issue loading your profile, please reconnect.");
                    }

                    if (lcApi.isRunningLunarClient(player)) {
                        player.sendMessage(Colors.get("&cReminder: &fWe have detected that you are using Lunar Client. To ensure your best experience on PvP Camp, please enable Lunar Client cooldowns."));
                    }
                }
            }
        }, 20);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(profile != null) {
            Game game = profile.getGame();

            if (game != null) {
                switch (profile.getState()) {
                    case SPECTATING:
                        game.spectateEnd(player);
                        break;
                    case IN_GAME:
                        game.eliminate(player, true);
                        break;
                }
            }

            if (profile.getParty() != null) {
                profile.getParty().leave(player);
            }

            if (profile.getTournament() != null) {
                profile.getTournament().leave(player);
            }

            plugin.getGameQueueManager().removeFromQueue(player);

            event.setQuitMessage(null);

            Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getGameProfileManager().exportToDatabase(player.getUniqueId(), true, false), 2);
        }
    }
}
