package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.Practice;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerItemConsumeListener implements Listener {

    private Practice plugin;
    public PlayerItemConsumeListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        String name = meta.getDisplayName();

        if(name != null && name.contains("Golden Head") && item.getType().equals(Material.GOLDEN_APPLE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
        }
    }
}
