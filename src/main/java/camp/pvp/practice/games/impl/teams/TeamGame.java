package camp.pvp.practice.games.impl.teams;

import camp.pvp.practice.games.GameTeam;
import camp.pvp.practice.Practice;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.kits.HCFKit;
import camp.pvp.practice.utils.Colors;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.*;

public abstract class TeamGame extends Game {

    private @Getter GameTeam blue, red;
    protected TeamGame(Practice plugin, UUID uuid) {
        super(plugin, uuid);
        this.blue = new GameTeam(GameTeam.Color.BLUE, this);
        this.red = new GameTeam(GameTeam.Color.RED, this);
    }

    @Override
    public void eliminate(Player player, boolean leftGame) {
        super.eliminate(player, leftGame);

        if(getBlue().isEliminated() || getRed().isEliminated()) {
            this.end();
        }
    }

    @Override
    public void handleHit(Player victim, Player attacker, EntityDamageByEntityEvent event) {
        GameParticipant victimParticipant = this.getParticipants().get(victim.getUniqueId());
        GameParticipant participant = this.getParticipants().get(attacker.getUniqueId());

        if(victimParticipant != null && participant != null) {
            if(victimParticipant.isAlive() && participant.isAlive()) {
                if(victimParticipant.equals(participant)) {
                    return;
                }

                if(victimParticipant.getTeam().equals(participant.getTeam())) {
                    attacker.sendMessage(ChatColor.RED + victim.getName() + " is your teammate.");
                    event.setCancelled(true);
                }

                victimParticipant.setAttacker(attacker.getUniqueId());

                if(event.getDamager() instanceof Arrow && participant.getAppliedHcfKit() != null && participant.getAppliedHcfKit().equals(HCFKit.ARCHER) && (victimParticipant.getAppliedHcfKit() == null || !victimParticipant.getAppliedHcfKit().equals(HCFKit.ARCHER))) {
                    victimParticipant.archerTag();
                }

                if(event.getDamager() instanceof Player) {
                    if (victim.getNoDamageTicks() == 0) {
                        participant.hits++;
                        participant.currentCombo++;

                        participant.setHealth(Math.round(victim.getHealth()));
                        participant.setMaxHealth(Math.round(victim.getMaxHealth()));
                        participant.setHunger(victim.getFoodLevel());
                        participant.setPotionEffects(new ArrayList<>(victim.getActivePotionEffects()));

                        if (participant.isComboMessages()) {
                            switch ((int) participant.getCurrentCombo()) {
                                case 5 -> {
                                    attacker.playSound(attacker.getLocation(), Sound.FIREWORK_LAUNCH, 1F, 1F);
                                    attacker.sendMessage(Colors.get("&a ** 5 Hit Combo! **"));
                                }
                                case 10 -> {
                                    attacker.playSound(attacker.getLocation(), Sound.EXPLODE, 1F, 1F);
                                    attacker.sendMessage(Colors.get("&6&o ** 10 HIT COMBO! **"));
                                }
                                case 20 -> {
                                    attacker.playSound(attacker.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 1F);
                                    attacker.sendMessage(Colors.get("&4&l&o ** 20 HIT COMBO!!! **"));
                                }
                            }
                        }
                    }
                }

                if (participant.currentCombo > participant.longestCombo) {
                    participant.longestCombo = participant.currentCombo;
                }
            } else {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    // TODO: Reimplement Lunar Client teams.
//    public void updateLcTeamLocations(GameTeam team) {
//        Map<UUID, Map<String, Double>> playerLocations = new HashMap<>();
//        Set<Player> players = new HashSet<>();
//
//        for(GameParticipant participant : team.getAliveParticipants().values()) {
//            Player player = participant.getPlayer();
//            Location location = player.getLocation();
//            Map<String, Double> locations = new HashMap<>();
//            locations.put("x", location.getX());
//            locations.put("y", location.getY());
//            locations.put("z", location.getZ());
//            playerLocations.put(participant.getUuid(), locations);
//            players.add(player);
//        }
//
//        LCPacketTeammates lcPacketTeammates = new LCPacketTeammates(null, System.currentTimeMillis(), playerLocations);
//        LunarClientAPI lcApi = LunarClientAPI.getInstance();
//
//        for(Player player : players) {
//            if(lcApi.isRunningLunarClient(player)) {
//                lcApi.sendPacket(player, lcPacketTeammates);
//            }
//        }
//    }
}
