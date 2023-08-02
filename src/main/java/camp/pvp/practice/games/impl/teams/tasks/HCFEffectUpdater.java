package camp.pvp.practice.games.impl.teams.tasks;

import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameManager;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.impl.teams.HCFTeams;
import camp.pvp.practice.kits.HCFKit;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

public class HCFEffectUpdater implements Runnable{

    private GameManager gameManager;
    public HCFEffectUpdater(GameManager manager) {
        this.gameManager = manager;
    }

    @Override
    public void run() {
        for(Game game : gameManager.getActiveGames()) {
            if(game instanceof HCFTeams) {
                HCFTeams hcfTeams = (HCFTeams) game;
                for(GameParticipant participant : hcfTeams.getAlive().values()) {
                    if(participant.getAppliedHcfKit() != null && participant.getAppliedHcfKit() == HCFKit.BARD) {
                        Player player = participant.getPlayer();

                        if (player.getItemInHand() != null) {
                            PotionEffect effect = null;
                            switch (player.getItemInHand().getType()) {
                                case GHAST_TEAR:
                                    effect = new PotionEffect(PotionEffectType.REGENERATION, 100, 0);
                                    break;
                                case BLAZE_POWDER:
                                    effect = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0);
                                    break;
                                case IRON_INGOT:
                                    effect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0);
                                    break;
                                case FEATHER:
                                    effect = new PotionEffect(PotionEffectType.JUMP, 100, 1);
                                    break;

                            }

                            if (effect != null) {
                                for (GameParticipant p : participant.getTeam().getAliveParticipants().values()) {
                                    Player p1 = p.getPlayer();

                                    if(!(effect.getType().equals(PotionEffectType.INCREASE_DAMAGE) && p1.equals(player))) {
                                        boolean apply = true;
                                        for(PotionEffect pe : p1.getActivePotionEffects()) {
                                            if(pe.getType().equals(effect.getType())) {
                                                if(pe.getAmplifier() > effect.getAmplifier()) {
                                                    apply = false;
                                                }
                                            }
                                        }

                                        if(apply) {
                                            if (p1.getLocation().distance(player.getLocation()) < 20) {
                                                p1.addPotionEffect(effect, true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
