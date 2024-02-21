package camp.pvp.practice.games;

import camp.pvp.practice.kits.CustomDuelKit;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.kits.HCFKit;
import camp.pvp.practice.Practice;
import camp.pvp.practice.cooldowns.PlayerCooldown;
import camp.pvp.practice.profiles.GameProfile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter @Setter
public class GameParticipant {

    private final UUID uuid;
    private final String name;
    private Game game;
    private GameTeam team;
    private boolean alive, respawn, invincible, currentlyPlaying, kitApplied, comboMessages;

    // HCFTEAMS ONLY
    private HCFKit appliedHcfKit;
    private HCFKit previousHcfKit;
    private int energy;
    private List<PotionEffect> previousEffects;
    private Date lastArcherTag;

    private Map<PlayerCooldown.Type, PlayerCooldown> cooldowns;

    private UUID attacker, attacking;
    private EntityDamageEvent.DamageCause lastDamageCause;
    private long lastInvalidHitTick, lastValidHitTick;

    public long health, maxHealth, hunger,
            hits, currentCombo, longestCombo,
            thrownPotions, missedPotions;
    private int kills, deaths;

    private List<PotionEffect> potionEffects;
    private PostGameInventory postGameInventory;
    private Location spawnLocation;
    private DuelKit duelKit;
    private CustomDuelKit appliedCustomKit;

    private BukkitTask respawnTask;

    public GameParticipant(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.alive = true;
        this.previousEffects = new ArrayList<>();
        this.cooldowns = new HashMap<>();

        this.currentlyPlaying = true;

        this.appliedHcfKit = null;
        this.energy = 0;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public GameProfile getProfile() {
        return Practice.getInstance().getGameProfileManager().getLoadedProfiles().get(uuid);
    }

    public void archerTag() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 10);
        lastArcherTag = calendar.getTime();

        getPlayer().sendMessage(ChatColor.RED + "You have been archer tagged for 10 seconds!");
    }

    public boolean isArcherTagged() {
        if(lastArcherTag != null) {
            return lastArcherTag.after(new Date());
        }

        return false;
    }

    public void clearCooldowns() {
        for (PlayerCooldown cooldown : cooldowns.values()) {
            cooldown.remove();
        }
    }

    public void kill() {
        alive = false;
        invincible = true;
        deaths++;

        clearCooldowns();

        if(attacker != null) {
            GameParticipant a = game.getParticipants().get(attacker);

            if(a != null) a.setKills(a.getKills() + 1);
        }

        if(!isRespawn()) {
            currentlyPlaying = false;

            if(respawnTask != null) respawnTask.cancel();
        }
    }

    public boolean isRespawn() {
        if(team != null) return team.isRespawn();

        return respawn;
    }

    public void respawn(int delay) {
        respawnTask = Bukkit.getScheduler().runTaskTimer(Practice.getInstance(), new Runnable() {
            int time = delay;
            @Override
            public void run() {
                if(time == 0) {
                    if(getAppliedCustomKit() != null) {
                        getAppliedCustomKit().apply(GameParticipant.this);
                    } else {
                        getDuelKit().apply(GameParticipant.this);
                    }

                    getPlayer().teleport(getRespawnLocation());
                    setAlive(true);

                    setLastDamageCause(null);
                    setAttacker(null);

                    Practice.getInstance().getGameProfileManager().updateGlobalPlayerVisibility();

                    Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
                        setInvincible(false);
                    }, 20L);

                    getRespawnTask().cancel();
                } else {
                    getPlayer().sendMessage(ChatColor.GREEN + "Respawning in " + time + " second(s).");
                    time--;
                }
            }
        }, 0, 20);
    }

    public void applyTemporaryEffect(PotionEffectType type, int duration, int strength) {
        for(PotionEffect pe : getPlayer().getActivePotionEffects()) {
            getPreviousEffects().add(pe);
            if(type.equals(pe.getType())) {
                getPlayer().removePotionEffect(pe.getType());
            }
        }

        previousHcfKit = appliedHcfKit;

        getPlayer().addPotionEffect(new PotionEffect(type, duration * 20, strength));

        Bukkit.getScheduler().runTaskLater(Practice.instance, () -> applyPreviousEffects(duration * 20), duration * 20L);
    }

    public void applyPreviousEffects(int duration) {

        if(previousHcfKit == appliedHcfKit) {
            if (getPlayer() != null && alive && postGameInventory == null) {
                for (PotionEffect pe : getPlayer().getActivePotionEffects()) {
                    getPlayer().removePotionEffect(pe.getType());
                }

                for (PotionEffect pe : getPreviousEffects()) {
                    if(pe.getDuration() - duration > 0) {
                        PotionEffect newEffect = new PotionEffect(pe.getType(), pe.getDuration() - duration, pe.getAmplifier());
                        getPlayer().addPotionEffect(newEffect);
                    }
                }
            }
            getPreviousEffects().clear();
        }
    }

    public Location getRespawnLocation() {
        if(game.getArena().getType().isRandomSpawnLocation()) {
            Map<Location, Double> distances = new HashMap<>();
            for(Location l : game.getArena().getRandomSpawnLocations()) {
                double nearestDistance = Double.MAX_VALUE;
                for(Player player : game.getAlivePlayers()) {
                    double distance = player.getLocation().distance(l);
                    if(distance < nearestDistance) {
                        nearestDistance = distance;
                    }
                }

                distances.put(l, nearestDistance);
            }

            for(Map.Entry<Location, Double> entry : distances.entrySet()) {
                if(Objects.equals(entry.getValue(), Collections.min(distances.values()))) {
                    return entry.getKey();
                }
            }
        }

        return spawnLocation;
    }

    public GameTeam.Color getTeamColor() {
        if(team != null) return team.getColor();

        List<GameParticipant> participants = new ArrayList<>(game.getParticipants().values());
        int x = 0;
        for(GameParticipant p : participants) {
            if(p.getUuid().equals(uuid)) {
                break;
            }
            x++;
        }

        if(x == 0) {
            return GameTeam.Color.BLUE;
        } else {
            return GameTeam.Color.RED;
        }
    }
}
