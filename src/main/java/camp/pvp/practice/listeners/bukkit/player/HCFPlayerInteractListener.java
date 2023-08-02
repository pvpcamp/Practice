package camp.pvp.practice.listeners.bukkit.player;

import camp.pvp.practice.Practice;
import camp.pvp.practice.cooldowns.PlayerCooldown;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.impl.teams.HCFTeams;
import camp.pvp.practice.profiles.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class HCFPlayerInteractListener implements Listener {

    private Practice plugin;
    public HCFPlayerInteractListener(Practice plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        Game game = profile.getGame();
        Action action = event.getAction();

        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK) && game != null) {
            if(game instanceof HCFTeams && game.getCurrentPlaying().get(player.getUniqueId()) != null && game.getState().equals(Game.State.ACTIVE)) {
                GameParticipant participant = game.getCurrentPlaying().get(player.getUniqueId());
                PlayerCooldown cooldown = null;

                if(participant.getAppliedHcfKit() != null) {
                    switch(participant.getAppliedHcfKit()) {
                        case BARD:
                            switch (player.getItemInHand().getType()) {
                                case BLAZE_POWDER:
                                    cooldown = participant.getCooldowns().get(PlayerCooldown.Type.ENERGY_STRENGTH);
                                    if (cooldown == null || cooldown.isExpired()) {

                                        if(participant.getEnergy() < 45) {
                                            player.sendMessage(ChatColor.RED + "You must have 45 energy to use this ability.");
                                            return;
                                        }

                                        participant.setEnergy(participant.getEnergy() - 45);

                                        for (GameParticipant p : participant.getTeam().getAliveParticipants().values()) {
                                            Player teamPlayer = p.getPlayer();
                                            if (teamPlayer != player && teamPlayer.getLocation().distance(player.getLocation()) < 20) {
                                                p.applyTemporaryEffect(PotionEffectType.INCREASE_DAMAGE, 6, 1);
                                            }
                                        }

                                        participant.getCooldowns().put(PlayerCooldown.Type.ENERGY_STRENGTH, new PlayerCooldown(PlayerCooldown.Type.ENERGY_STRENGTH, participant, player));
                                    } else {
                                        player.sendMessage(cooldown.getBlockedMessage());
                                    }
                                    break;
                                case GHAST_TEAR:
                                    cooldown = participant.getCooldowns().get(PlayerCooldown.Type.ENERGY_REGEN);
                                    if (cooldown == null || cooldown.isExpired()) {

                                        if(participant.getEnergy() < 40) {
                                            player.sendMessage(ChatColor.RED + "You must have 40 energy to use this ability.");
                                            return;
                                        }

                                        participant.setEnergy(participant.getEnergy() - 40);

                                        for (GameParticipant p : participant.getTeam().getAliveParticipants().values()) {
                                            Player teamPlayer = p.getPlayer();
                                            if (teamPlayer.getLocation().distance(player.getLocation()) < 20) {
                                                p.applyTemporaryEffect(PotionEffectType.REGENERATION, 5, 1);
                                            }
                                        }
                                        participant.getCooldowns().put(PlayerCooldown.Type.ENERGY_REGEN, new PlayerCooldown(PlayerCooldown.Type.ENERGY_REGEN, participant, player));
                                    } else {
                                        player.sendMessage(cooldown.getBlockedMessage());
                                    }
                                    break;
                                case SUGAR:
                                    cooldown = participant.getCooldowns().get(PlayerCooldown.Type.ENERGY_SPEED);
                                    if (cooldown == null || cooldown.isExpired()) {

                                        if(participant.getEnergy() < 25) {
                                            player.sendMessage(ChatColor.RED + "You must have 25 energy to use this ability.");
                                            return;
                                        }

                                        for (GameParticipant p : participant.getTeam().getAliveParticipants().values()) {
                                            Player teamPlayer = p.getPlayer();
                                            if (teamPlayer.getLocation().distance(player.getLocation()) < 20) {
                                                p.applyTemporaryEffect(PotionEffectType.SPEED, 10, 2);
                                            }
                                        }

                                        participant.getCooldowns().put(PlayerCooldown.Type.ENERGY_SPEED, new PlayerCooldown(PlayerCooldown.Type.ENERGY_SPEED, participant, player));
                                    } else {
                                        player.sendMessage(cooldown.getBlockedMessage());
                                    }
                                    break;
                                case IRON_INGOT:
                                    cooldown = participant.getCooldowns().get(PlayerCooldown.Type.ENERGY_RESISTANCE);
                                    if (cooldown == null || cooldown.isExpired()) {

                                        if(participant.getEnergy() < 35) {
                                            player.sendMessage(ChatColor.RED + "You must have 35 energy to use this ability.");
                                            return;
                                        }

                                        participant.setEnergy(participant.getEnergy() - 35);

                                        for (GameParticipant p : participant.getTeam().getAliveParticipants().values()) {
                                            Player teamPlayer = p.getPlayer();
                                            if (teamPlayer.getLocation().distance(player.getLocation()) < 20) {
                                                p.applyTemporaryEffect(PotionEffectType.DAMAGE_RESISTANCE, 5, 2);
                                            }
                                        }

                                        participant.getCooldowns().put(PlayerCooldown.Type.ENERGY_RESISTANCE, new PlayerCooldown(PlayerCooldown.Type.ENERGY_RESISTANCE, participant, player));
                                    } else {
                                        player.sendMessage(cooldown.getBlockedMessage());
                                    }
                                    break;
                                case FEATHER:
                                    cooldown = participant.getCooldowns().get(PlayerCooldown.Type.ENERGY_JUMP);
                                    if (cooldown == null || cooldown.isExpired()) {

                                        if(participant.getEnergy() < 35) {
                                            player.sendMessage(ChatColor.RED + "You must have 35 energy to use this ability.");
                                            return;
                                        }

                                        participant.setEnergy(participant.getEnergy() - 35);

                                        for (GameParticipant p : participant.getTeam().getAliveParticipants().values()) {
                                            Player teamPlayer = p.getPlayer();
                                            if (teamPlayer.getLocation().distance(player.getLocation()) < 20) {
                                                p.applyTemporaryEffect(PotionEffectType.JUMP, 10, 5);
                                            }
                                        }

                                        participant.getCooldowns().put(PlayerCooldown.Type.ENERGY_JUMP, new PlayerCooldown(PlayerCooldown.Type.ENERGY_JUMP, participant, player));
                                    } else {
                                        player.sendMessage(cooldown.getBlockedMessage());
                                    }
                                    break;
                            }
                            break;
                        case ARCHER:
                            switch(player.getItemInHand().getType()) {
                                case SUGAR:
                                    cooldown = participant.getCooldowns().get(PlayerCooldown.Type.ENERGY_SPEED);
                                    if (cooldown == null || cooldown.isExpired()) {

                                        if(participant.getEnergy() < 25) {
                                            player.sendMessage(ChatColor.RED + "You must have 25 energy to use this ability.");
                                            return;
                                        }

                                        participant.setEnergy(participant.getEnergy() - 25);

                                        participant.applyTemporaryEffect(PotionEffectType.SPEED, 10, 3);

                                        participant.getCooldowns().put(PlayerCooldown.Type.ENERGY_SPEED, new PlayerCooldown(PlayerCooldown.Type.ENERGY_SPEED, participant, player));
                                    } else {
                                        player.sendMessage(cooldown.getBlockedMessage());
                                    }
                                    break;
                            }
                    }
                }
            }
        }
    }
}
