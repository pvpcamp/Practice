package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDropItemListener implements Listener {

    private Practice plugin;
    public PlayerDropItemListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        Game game = profile.getGame();

        if(game != null && game.getAlivePlayers().contains(player)) {

            Item item = event.getItemDrop();

            game.addEntity(item);

            if (item.getItemStack().getType().equals(Material.GLASS_BOTTLE) || item.getItemStack().getType().equals(Material.BOWL)) {
                item.remove();
                return;
            }

            GameParticipant participant = game.getParticipants().get(player.getUniqueId());
            if(participant != null && !participant.isKitApplied()) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot drop anything until you apply your kit.");
                return;
            }

            if(player.getInventory().getHeldItemSlot() == profile.getNoDropHotbarSlot() && player.getOpenInventory().getTopInventory() == null) {
                event.setCancelled(true);
                player.sendMessage(player.getOpenInventory().getType().toString());
                player.sendMessage(ChatColor.RED + "You cannot drop the item in this slot.");
                return;
            }

            new BukkitRunnable() {
                public void run() {
                    item.remove();
                }
            }.runTaskLater(plugin, 400L);
        } else if (profile.getState().equals(GameProfile.State.KIT_EDITOR)) {
            Item item = event.getItemDrop();
            item.remove();
        } else {
            event.setCancelled(!profile.isBuildMode());
        }
    }
}
