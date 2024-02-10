package camp.pvp.practice.listeners.bukkit.inventory;

import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.impl.teams.HCFTeams;
import camp.pvp.practice.kits.HCFKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryClickListener implements Listener {

    private Practice plugin;
    public InventoryClickListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        switch(profile.getState()) {
            case KIT_EDITOR:
                break;
            case IN_GAME:
                Game game = profile.getGame();
                if(game != null) {

                    if(event.getClickedInventory() instanceof PlayerInventory) {
                        profile.setLastClickedItem(event.getCurrentItem());
                    }

                    GameParticipant participant = game.getAlive().get(player.getUniqueId());
                    if(participant != null) {
                        if(!participant.isKitApplied()) {
                            event.setCancelled(true);
                        }
                    }
                }
                break;
            default:
                if(!profile.isBuildMode()) {
                    event.setCancelled(true);
                }
        }
    }
}
