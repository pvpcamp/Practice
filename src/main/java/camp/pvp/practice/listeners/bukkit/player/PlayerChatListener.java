package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.Practice;
import camp.pvp.practice.kits.CustomDuelKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private Practice plugin;
    public PlayerChatListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(profile != null) {
            CustomDuelKit cdk  = profile.getEditingCustomKit();
            if(cdk != null) {
                player.sendMessage(Colors.get("&aKit " + cdk.getName() + " &ahas been renamed to " + event.getMessage() + "&a."));
                cdk.setName(event.getMessage());
                profile.setEditingCustomKit(null);
                event.setCancelled(true);
            }
        }
    }
}
