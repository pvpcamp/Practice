package camp.pvp.practice.games.impl.teams;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.cooldowns.PlayerCooldown;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.GameTeam;
import camp.pvp.practice.games.tasks.TeleportFix;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class HCFTeams extends TeamDuel {

    public HCFTeams(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public void initialize() {
        List<Arena> list = new ArrayList<>();
        if(getArena() == null) {
            for(Arena a : getPlugin().getArenaManager().getArenas()) {
                if(a.isEnabled()) {
                    if(a.getType().equals(Arena.Type.HCF_TEAMFIGHT)) {
                        list.add(a);
                    }
                }
            }

            Collections.shuffle(list);
            this.setArena(list.get(0));
        }

        if(getArena() == null) {
            for(Player p : getAlivePlayers()) {
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(p.getUniqueId());
                p.sendMessage(ChatColor.RED + "There are no arenas currently available for the ladder selected. Please notify a staff member.");
                profile.setGame(null);
                profile.playerUpdate(true);
            }
            return;
        }

        Arena arena = getArena();

        arena.prepare();

        this.setKit(GameKit.NO_DEBUFF.getBaseKit());

        for(Party party : this.getParties()) {
            party.setGame(this);
        }

        this.setState(State.STARTING);

        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append("\n&6&lTeam duel starting in 5 seconds.");
        sb.append("\n &7● &6Map: &f" + arena.getDisplayName());

        ArenaPosition blueSpawn = arena.getPositions().get("spawn1");
        List<GameParticipant> blueParticipants = new ArrayList<>(getBlue().getParticipants().values());
        sb.append("\n &7● &9Blue Team: &f");

        int blueCount = 0;

        for(GameParticipant participant : getBlue().getParticipants().values()) {
            Player player = participant.getPlayer();
            sb.append(ChatColor.WHITE + participant.getName());

            blueParticipants.remove(participant);
            blueCount++;
            if (blueCount == getBlue().getParticipants().size()) {
                sb.append("&7.");
            } else {
                sb.append("&7, ");
            }

            player.teleport(blueSpawn.getLocation());

            participant.setKitApplied(true);
            participant.getAppliedHcfKit().apply(player);
        }

        ArenaPosition redSpawn = arena.getPositions().get("spawn2");
        List<GameParticipant> redParticipants = new ArrayList<>(getRed().getParticipants().values());
        sb.append("\n &7● &cRed Team: &f");

        int redCount = 0;

        for(GameParticipant participant : getRed().getParticipants().values()) {
            Player player = participant.getPlayer();
            sb.append(ChatColor.WHITE + participant.getName());

            redParticipants.remove(participant);
            redCount++;
            if (redCount == getRed().getParticipants().size()) {
                sb.append("&7.");
            } else {
                sb.append("&7, ");
            }

            player.teleport(redSpawn.getLocation());

            participant.setKitApplied(true);
            participant.getAppliedHcfKit().apply(player);
        }

        sb.append("\n ");

        this.announce(sb.toString());

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

        Bukkit.getScheduler().runTaskLater(getPlugin(), new TeleportFix(this), 1);
        startingTimer(5);
    }

    @Override
    public void handleHit(Player victim, Player attacker, EntityDamageByEntityEvent event) {
        super.handleHit(victim, attacker, event);
        event.setDamage(event.getDamage() * 0.75D);
    }

    @Override
    public void handleInteract(Player player, PlayerInteractEvent event) {
        GameParticipant participant = getCurrentPlaying().get(player.getUniqueId());
        PlayerCooldown cooldown;

        if(participant.getAppliedHcfKit() == null) {
            return;
        }

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
                                    p.applyTemporaryEffect(PotionEffectType.REGENERATION, 5, 2);
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

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();

        GameParticipant self = getAlive().get(profile.getUuid());

        GameTeam friendlyTeam = self.getTeam();
        GameTeam enemyTeam = friendlyTeam.getColor().equals(GameTeam.Color.BLUE) ? getBlue() : getRed();

        boolean showInGame = profile.isSidebarInGame(),
                showCps = profile.isSidebarShowCps(),
                showDuration = profile.isSidebarShowDuration(),
                showPing = profile.isSidebarShowPing();

        switch(getState()) {
            case STARTING:
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                lines.add("");
                lines.add("&9Blue Team " + (friendlyTeam.getColor().equals(GameTeam.Color.BLUE) ? "(You)" : "") + ": &f" + getBlue().getAliveParticipants().size() + "/" + getBlue().getParticipants().size());
                lines.add("&cRed Team " + (friendlyTeam.getColor().equals(GameTeam.Color.RED) ? "(You)" : "") + ": &f" + getRed().getAliveParticipants().size() + "/" + getRed().getParticipants().size());
                break;
            case ACTIVE:
                if(!showInGame) {
                    return null;
                }

                int ping = 0;
                ping = PlayerUtils.getPing(self.getPlayer());

                boolean addSpace = false;
                if(showDuration) {
                    addSpace = true;
                    lines.add("&6Duration: &f" + TimeUtil.get(new Date(), getStarted()));
                }

                if(showPing) {
                    addSpace = true;
                    lines.add("&6Your Ping: &f" + ping + " ms");
                }

                if(showCps) {
                    addSpace = true;
                    lines.add("&6Your CPS: &f" + profile.getCps());
                }

                if(self.getAppliedHcfKit() != null) {
                    switch(self.getAppliedHcfKit()) {
                        case BARD:
                        case ARCHER:
                            lines.add("&6Energy: &f" + self.getEnergy());
                            addSpace = true;
                    }
                }

                if(self.isArcherTagged()) {
                    addSpace = true;
                    lines.add("&cArcher Tag: &f" + TimeUnit.MILLISECONDS.toSeconds(self.getLastArcherTag().getTime() - new Date().getTime()));
                }

                if(addSpace) {
                    lines.add("");
                }

                lines.add(friendlyTeam.getColor().getChatColor() + "Alive Teammates:");

                int i = 0;
                for(GameParticipant participant : friendlyTeam.getAliveParticipants().values()) {
                    if(i < 5) {
                        Player player = participant.getPlayer();
                        lines.add((participant.isArcherTagged() ? " &c&o" : " &f") + participant.getName() + " &c" + Math.round(player.getHealth()) + " ❤");
                    } else {
                        lines.add("&f...");
                    }

                    i++;
                }

                lines.add(" ");


                lines.add("&9Blue Team " + (friendlyTeam.getColor().equals(GameTeam.Color.BLUE) ? "(You)" : "") + ": &f" + getBlue().getAliveParticipants().size() + "/" + getBlue().getParticipants().size());
                lines.add("&cRed Team " + (friendlyTeam.getColor().equals(GameTeam.Color.RED) ? "(You)" : "") + ": &f" + getRed().getAliveParticipants().size() + "/" + getRed().getParticipants().size());
                break;
            case ENDED:
                lines.add("&6&lYour Team Wins!");

                if(showDuration) {
                    lines.add("&6Duration: &f&n" + TimeUtil.get(getEnded(), getStarted()));
                }

                break;
        }
        return lines;
    }

    @Override
    public List<String> getSpectatorScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();

        switch(getState()) {
            case STARTING:
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                lines.add(" ");
                lines.add("&9Blue Team: &f" + getBlue().getAliveParticipants().size() + "/" + getBlue().getParticipants().size());
                lines.add("&cRed Team: &f" + getRed().getAliveParticipants().size() + "/" + getRed().getParticipants().size());
                break;
            case ACTIVE:
                lines.add("&6Duration: &f" + TimeUtil.get(new Date(), getStarted()));
                lines.add(" ");
                lines.add("&9Blue Team: &f" + getBlue().getAliveParticipants().size() + "/" + getBlue().getParticipants().size());
                lines.add("&cRed Team: &f" + getRed().getAliveParticipants().size() + "/" + getRed().getParticipants().size());

                break;
            case ENDED:
                lines.add("&6Duration: &f&n" + TimeUtil.get(getEnded(), getStarted()));
                lines.add(" ");
                lines.add("&9Blue Team: &f" + getBlue().getAliveParticipants().size() + "/" + getBlue().getParticipants().size());
                lines.add("&cRed Team: &f" + getRed().getAliveParticipants().size() + "/" + getRed().getParticipants().size());

                break;
        }

        return lines;
    }

    @Override
    public String getScoreboardTitle() {
        return "HCT";
    }
}
