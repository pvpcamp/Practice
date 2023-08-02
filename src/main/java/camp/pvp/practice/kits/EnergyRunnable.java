package camp.pvp.practice.kits;

import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.impl.teams.HCFTeams;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class EnergyRunnable implements Runnable{

    private Practice plugin;
    public EnergyRunnable(Practice plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for(Game game : plugin.getGameManager().getActiveGames()) {
            if(game instanceof HCFTeams) {
                for(GameParticipant participant : game.getAlive().values()) {

                    Player player = participant.getPlayer();
                    PlayerInventory pi = player.getInventory();
                    final HCFKit hcfKit = participant.getAppliedHcfKit();
                    HCFKit currentKit = null;

                    Material helmet = pi.getHelmet() == null ? Material.AIR : pi.getHelmet().getType();
                    Material chestplate = pi.getChestplate() == null ? Material.AIR : pi.getChestplate().getType();
                    Material leggings = pi.getLeggings() == null ? Material.AIR : pi.getLeggings().getType();
                    Material boots = pi.getBoots() == null ? Material.AIR : pi.getBoots().getType();
                    if(
                        // Check for Diamond armor.
                        boots.equals(Material.DIAMOND_BOOTS)
                        && leggings.equals(Material.DIAMOND_LEGGINGS)
                        && chestplate.equals(Material.DIAMOND_CHESTPLATE)
                        && helmet.equals(Material.DIAMOND_HELMET)
                    ) {
                        currentKit = HCFKit.DIAMOND;
                    } else if (
                        // Check for Bard armor.
                        boots.equals(Material.GOLD_BOOTS)
                        && leggings.equals(Material.GOLD_LEGGINGS)
                        && chestplate.equals(Material.GOLD_CHESTPLATE)
                        && helmet.equals(Material.GOLD_HELMET)
                    ) {
                        currentKit = HCFKit.BARD;
                    } else if(
                        // Check for Archer armor.
                        boots.equals(Material.LEATHER_BOOTS)
                        && leggings.equals(Material.LEATHER_LEGGINGS)
                        && chestplate.equals(Material.LEATHER_CHESTPLATE)
                        && helmet.equals(Material.LEATHER_HELMET)
                    ) {
                        currentKit = HCFKit.ARCHER;
                    }

                    if(currentKit != hcfKit) {
                        participant.setAppliedHcfKit(currentKit);
                        participant.setEnergy(0);

                        if(currentKit != null) {
                            currentKit.applyEffects(player);
                            player.sendMessage(ChatColor.GREEN + "You have applied HCF kit " + currentKit.getColor() + currentKit + ChatColor.GREEN + ".");
                        } else {
                            for(PotionEffect effect : player.getActivePotionEffects()) {
                                if(effect.getDuration() > 10000) {
                                    player.removePotionEffect(effect.getType());
                                }
                            }
                        }
                    }

                    if(participant.getAppliedHcfKit() != null && (participant.getAppliedHcfKit().equals(HCFKit.ARCHER) || participant.getAppliedHcfKit().equals(HCFKit.BARD))) {
                        int energy = participant.getEnergy();
                        if(energy < 100) {
                            energy += 1;
                            participant.setEnergy(energy);
                        }
                    }
                }
            }
        }
    }
}
