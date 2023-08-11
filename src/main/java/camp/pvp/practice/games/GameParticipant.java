package camp.pvp.practice.games;

import camp.pvp.practice.kits.HCFKit;
import camp.pvp.practice.Practice;
import camp.pvp.practice.cooldowns.PlayerCooldown;
import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@Getter @Setter
public class GameParticipant {

    private final UUID uuid;
    private final String name;
    private GameTeam team;
    private boolean alive, currentlyPlaying, kitApplied, comboMessages;

    // HCFTEAMS ONLY
    private HCFKit appliedHcfKit;
    private HCFKit previousHcfKit;
    private int energy;
    private List<PotionEffect> previousEffects;
    private Date lastArcherTag;

    private Map<PlayerCooldown.Type, PlayerCooldown> cooldowns;

    private UUID attacker, attacking;
    private EntityDamageEvent.DamageCause lastDamageCause;

    public long health, maxHealth, hunger,
            hits, currentCombo, longestCombo,
            thrownPotions, missedPotions;

    private List<PotionEffect> potionEffects;
    private PostGameInventory postGameInventory;
    private Location respawnLocation;

    public GameParticipant(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.alive = true;
        this.previousEffects = new ArrayList<>();
        this.cooldowns = new HashMap<>();

        this.appliedHcfKit = null;
        this.energy = 0;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
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

    public void eliminate() {
        this.alive = false;
        this.currentlyPlaying = false;
    }

    public void applyTemporaryEffect(PotionEffectType type, int duration, int strength) {
        for(PotionEffect pe : getPlayer().getActivePotionEffects()) {
            getPreviousEffects().add(pe);
            if(type.equals(pe.getType())) {
                getPlayer().removePotionEffect(pe.getType());
            }
        }

        this.previousHcfKit = appliedHcfKit;

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
}
