package camp.pvp.listeners.bukkit.inventory;

import camp.pvp.Practice;
import camp.pvp.games.Game;
import camp.pvp.games.GameParticipant;
import camp.pvp.games.impl.HCFTeams;
import camp.pvp.kits.HCFKit;
import camp.pvp.profiles.GameProfile;
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
                    GameParticipant participant = game.getAlive().get(player.getUniqueId());
                    if(participant != null) {
                        if(!participant.isKitApplied()) {
                            event.setCancelled(true);
                        }

                        if(game instanceof HCFTeams) {
                            PlayerInventory pi = player.getInventory();
                            HCFKit hcfKit = participant.getAppliedHcfKit(), currentKit = null;
                            ItemStack[] armor = pi.getArmorContents();
                            if(
                                    // Check for Diamond armor.
                                    armor[0].getType().equals(Material.DIAMOND_BOOTS)
                                    && armor[1].getType().equals(Material.DIAMOND_LEGGINGS)
                                    && armor[2].getType().equals(Material.DIAMOND_CHESTPLATE)
                                    && armor[3].getType().equals(Material.DIAMOND_HELMET)
                            ) {
                                currentKit = HCFKit.DIAMOND;
                            } else if (
                                    // Check for Bard armor.
                                    armor[0].getType().equals(Material.GOLD_BOOTS)
                                    && armor[1].getType().equals(Material.GOLD_LEGGINGS)
                                    && armor[2].getType().equals(Material.GOLD_CHESTPLATE)
                                    && armor[3].getType().equals(Material.GOLD_HELMET)
                            ) {
                                currentKit = HCFKit.BARD;
                            } else if(
                                    // Check for Archer armor.
                                    armor[0].getType().equals(Material.LEATHER_BOOTS)
                                    && armor[1].getType().equals(Material.LEATHER_LEGGINGS)
                                    && armor[2].getType().equals(Material.LEATHER_CHESTPLATE)
                                    && armor[3].getType().equals(Material.LEATHER_HELMET)
                            ) {
                                currentKit = HCFKit.ARCHER;
                            }

                            if(currentKit != hcfKit) {
                                participant.setAppliedHcfKit(currentKit);
                                participant.setEnergy(0);

                                if(currentKit != null) {
                                    player.sendMessage(ChatColor.GREEN + "You have applied HCF kit " + currentKit.getColor() + currentKit + ChatColor.GREEN + ".");
                                } else {

                                }
                            }
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
