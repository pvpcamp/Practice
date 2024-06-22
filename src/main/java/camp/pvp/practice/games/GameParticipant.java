package camp.pvp.practice.games;

import camp.pvp.practice.kits.BaseKit;
import camp.pvp.practice.kits.CustomGameKit;
import camp.pvp.practice.Practice;
import camp.pvp.practice.cooldowns.PlayerCooldown;
import camp.pvp.practice.profiles.GameProfile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private LivingState livingState;
    private Date aliveTime, deathTime;
    private boolean respawn, invincible, kitApplied;

    private Map<PlayerCooldown.Type, PlayerCooldown> cooldowns;

    private UUID attacker, attacking;
    private EntityDamageEvent.DamageCause lastDamageCause;
    private long lastInvalidHitTick, lastValidHitTick;

    public long health, maxHealth, hunger,
            hits, currentCombo, longestCombo, blockedHits, criticalHits,
            arrowShots, arrowHits,
            fireballShots,
            thrownPotions, missedPotions;
    private int kills, killStreak, deaths;

    private List<PotionEffect> potionEffects;
    private PostGameInventory postGameInventory;
    private Location spawnLocation;
    private BaseKit baseKit;
    private CustomGameKit appliedCustomKit;

    private BukkitTask respawnTask;

    public GameParticipant(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.livingState = LivingState.ALIVE;
        this.cooldowns = new HashMap<>();
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

        getPlayer().sendMessage(ChatColor.RED + "You have been archer tagged for 10 seconds!");
    }

    public boolean isComboMessages() {
        return getProfile().isComboMessages();
    }

    public void clearCooldowns() {
        for (PlayerCooldown cooldown : cooldowns.values()) {
            cooldown.remove();
        }
    }

    public void kill() {
        livingState = isRespawn() ? LivingState.AWAITING_RESPAWN : LivingState.DEAD;
        invincible = true;
        deaths++;
        killStreak = 0;

        clearCooldowns();

        if(attacker != null) {
            GameParticipant a = game.getParticipants().get(attacker);

            if(a != null) {
                a.setKills(a.getKills() + 1);
                a.setKillStreak(a.getKillStreak() + 1);
            }
        }

        if(!isRespawn()) {
            deathTime = new Date();
            if (respawnTask != null) respawnTask.cancel();
        }
    }

    public boolean isRespawn() {
        if(team != null) return team.isRespawn();

        return respawn;
    }

    public boolean isAlive() {
        return getLivingState().equals(LivingState.ALIVE);
    }

    public boolean isCurrentlyPlaying() {
        return getLivingState().equals(LivingState.ALIVE) || getLivingState().equals(LivingState.AWAITING_RESPAWN);
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
                        getBaseKit().apply(GameParticipant.this);
                    }

                    Location location = GameParticipant.this.game.getRespawnLocation(GameParticipant.this);
                    location.getBlock().setType(Material.AIR);
                    location.clone().add(0, 1, 0).getBlock().setType(Material.AIR);

                    getPlayer().teleport(location);
                    setLivingState(LivingState.ALIVE);

                    setLastDamageCause(null);
                    setAttacker(null);

                    Practice.getInstance().getGameProfileManager().updateGlobalPlayerVisibility();

                    Bukkit.getScheduler().runTaskLater(Practice.getInstance(), () -> {
                        setInvincible(false);
                    }, 20L);

                    getPlayer().sendMessage(ChatColor.GREEN + "You have respawned.");

                    getRespawnTask().cancel();
                } else {
                    getPlayer().sendMessage(ChatColor.GREEN + "Respawning in " + time + " second" + (time == 1 ? "" : "s") + ".");
                    time--;
                }
            }
        }, 0, 20);
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

        return GameTeam.Color.values()[x];
    }

    public enum LivingState {
        ALIVE, AWAITING_RESPAWN, DEAD;
    }
}
