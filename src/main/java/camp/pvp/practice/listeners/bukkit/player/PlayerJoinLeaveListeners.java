package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.stats.ProfileELO;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketModSettings;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketServerRule;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import io.github.thatkawaiisam.assemble.AssembleBoard;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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
        profile.getProfileElo().setName(player.getName());

        profile.playerUpdate(true);

        plugin.getGameProfileManager().updateGlobalPlayerVisibility();

        event.setJoinMessage(null);

        plugin.getAssemble().getBoards().put(player.getUniqueId(), new AssembleBoard(player, plugin.getAssemble()));

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                GameProfile p = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
                if (player.isOnline()) {
                    if (p == null) {
                        player.kickPlayer(ChatColor.RED + "There was an issue loading your profile, please reconnect.");
                    } else {
                        if(!player.hasPermission("practice.staff")) {
                            p.setStaffMode(false);
                        }
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

            plugin.getAssemble().getBoards().remove(player.getUniqueId());

            Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getGameProfileManager().exportToDatabase(player.getUniqueId(), true, false), 2);
        }
    }
}
