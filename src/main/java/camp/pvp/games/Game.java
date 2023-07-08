package camp.pvp.games;

import camp.pvp.Practice;
import camp.pvp.arenas.Arena;
import camp.pvp.kits.DuelKit;
import camp.pvp.profiles.GameProfile;
import camp.pvp.utils.Colors;
import camp.pvp.utils.EntityHider;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter @Setter
public abstract class Game {

    public enum State {
        INACTIVE, STARTING, ACTIVE, ENDED;
    }

    private final Practice plugin;
    private EntityHider entityHider;

    public final UUID uuid;
    public Map<UUID, GameParticipant> participants;
    public Map<UUID, GameSpectator> spectators;

    public State state;
    public Arena arena;
    public DuelKit kit;

    public int round;
    public Date created, started, ended;

    public BukkitTask startingTimer, endingTimer;

    public List<Entity> entities;

    protected Game(Practice plugin, UUID uuid) {
        this.plugin = plugin;
        this.entityHider = plugin.getEntityHider();
        this.uuid = uuid;
        this.participants = new HashMap<>();
        this.spectators = new HashMap<>();
        this.state = State.INACTIVE;
        this.round = 0;

        this.plugin.getGameManager().addGame(this);
    }

    public abstract List<String> getScoreboard(GameProfile profile);

    public abstract List<String> getSpectatorScoreboard(GameProfile profile);

    public abstract void init();

    public abstract void start();

    public abstract void end();

    public abstract void forceEnd();

    public void eliminate(Player player) {
        GameParticipant participant = getParticipants().get(player.getUniqueId());
        if(participant != null) {
            participant.setAlive(false);

            if(getAlive().size() > 1) {
                for (ItemStack item : player.getInventory()) {
                    if (item != null && !item.getType().equals(Material.AIR)) {
                        Item i = player.getWorld().dropItem(player.getLocation(), item);
                        this.addEntity(i);
                    }
                }
            }

            Location location = player.getLocation();
//            GameProfile profile = plugin.getGameProfileManager().
//            profile.getCosmetics().getEliminateAnimation().runAnimation(location, this);
//
//            for(Cooldown cooldown : profile.getCooldowns().values()) {
//                cooldown.expire();
//            }
//
//            for(Spectator spectator : getSpectators().values()) {
//                if(spectator.getTarget().equals(player)) {
//                    spectator.setTarget(null);
//                }
//            }
//
//            participant.setGameInventory(new GameInventory(participant));
            this.spectateStart(player);
            this.announce("&f" + player.getName() + "&a has been eliminated" + (participant.getAttacker() == null ? "." : " by &f" + Bukkit.getOfflinePlayer(participant.getAttacker()).getName() + "&a."));
        }
    }

    public void handleDamage(Player victim, EntityDamageEvent event) {
        GameParticipant participant = this.getParticipants().get(victim.getUniqueId());
        participant.setLastDamageCause(event.getCause());
        if(this.getState().equals(State.ACTIVE)) {
            double damage = event.getFinalDamage();
//            participant.setCurrentCombo(0);
//            if(kit != null && kit.getType().equals(Kit.Type.SUMO)) {
//                event.setDamage(0);
//                victim.setHealth(20);
//            }

            if(event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
                damage += 1;
            }

            if(victim.getHealth() - damage < 0) {
                victim.setHealth(victim.getMaxHealth());
                this.eliminate(victim);
                Bukkit.getScheduler().runTaskLater(plugin, ()-> victim.setHealth(20), 1);
            }
        } else {
            event.setCancelled(true);
        }
    }

    public void handleHit(Player victim, Player attacker, EntityDamageByEntityEvent event) {
        GameParticipant victimParticipant = this.getParticipants().get(victim.getUniqueId());
        GameParticipant participant = this.getParticipants().get(attacker.getUniqueId());
        if(victimParticipant != null && participant != null) {
            victimParticipant.setAttacker(attacker.getUniqueId());
            participant.hits++;
            participant.currentCombo++;

            if(participant.currentCombo > participant.longestCombo) {
                participant.longestCombo = participant.currentCombo;
            }
        } else {
            event.setCancelled(true);
        }
    }

    public void join(Player player) {
        GameProfile profile = plugin.getGameProfileManager().find(player.getUniqueId(), true);
        profile.setGame(this);

        GameParticipant participant = new GameParticipant(player.getUniqueId(), player.getName());
        this.participants.put(player.getUniqueId(), participant);
    }

    public void spectateStart(Player player) {
        spectateStart(player, (Location) null);
    }

    public void spectateStart(Player player, Player target) {
        spectateStart(player, target.getLocation());
    }

    public void spectateStart(Player player, Location location) {
        GameProfile profile = plugin.getGameProfileManager().find(player.getUniqueId(), true);

        this.getSpectators().put(player.getUniqueId(), new GameSpectator(player.getUniqueId(), player.getName()));

        if(!this.getParticipants().containsKey(player.getUniqueId())) {
            String message = "&b" + player.getName() + "&f has started spectating.";
            player.sendMessage(ChatColor.GREEN + "You have started spectating.");
            if(player.hasPermission("practice.staff")) {
                this.staffAnnounce("&7[SILENT] " + message);
            } else {
                this.announce(message);
            }
        }

        profile.setGame(this);
        profile.givePlayerItems();

        plugin.getGameProfileManager().updateGlobalPlayerVisibility();
        updateEntities();

        player.setAllowFlight(true);
        player.setFlying(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1, true, false));
        if(location != null) {
            player.teleport(location);
        }
    }

    public void spectateEnd(Player player) {
        GameProfile profile = plugin.getGameProfileManager().find(player.getUniqueId(), true);

        if(!this.getState().equals(State.ENDED)) {
            if (!this.getParticipants().containsKey(player.getUniqueId())) {
                String message = "&f" + player.getName() + "&6 has stopped spectating.";
                if (player.hasPermission("practice.staff")) {
                    this.staffAnnounce("&7[Staff] " + message);
                } else {
                    this.announce(message);
                }
            }
        }

        this.getSpectators().remove(player.getUniqueId());

        profile.setGame(null);
        profile.playerUpdate();

        plugin.getGameProfileManager().updateGlobalPlayerVisibility();
        updateEntities();
    }

    public void leave(Player player) {
        if(getAlive().containsKey(player.getUniqueId())) {
            eliminate(player);
        } else {
            spectateEnd(player);
        }
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>(getAlivePlayers());
        players.addAll(getSpectatorsPlayers());
        return players;
    }

    public Map<UUID, GameParticipant> getAlive() {
        Map<UUID, GameParticipant> alive = new HashMap<>();
        for(Map.Entry<UUID, GameParticipant> entry : participants.entrySet()) {
            if(entry.getValue().isAlive()) {
                alive.put(entry.getKey(), entry.getValue());
            }
        }
        return alive;
    }

    public List<Player> getAlivePlayers() {
        List<Player> players = new ArrayList<>();
        for(UUID uuid : getAlive().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                players.add(player);
            }
        }
        return players;
    }

    public List<Player> getCurrentPlaying() {
        return getAlivePlayers();
    }

    public List<Player> getSpectatorsPlayers() {
        List<Player> players = new ArrayList<>();
        for(UUID uuid : spectators.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                players.add(player);
            }
        }
        return players;
    }

    public <T> void playEffect(Location location, Effect effect, T t) {
        for(Player player : getAllPlayers()) {
            player.playEffect(location, effect, t);
        }
    }

    public void playSound(Location location, Sound sound, float v1, float v2) {
        for(Player player : getAllPlayers()) {
            player.playSound(location, sound, v1, v2);
        }
    }

    public void addEntity(Entity entity) {
        getEntities().add(entity);
        updateEntities();
    }

    public void updateEntities() {
        List<Player> players = getAllPlayers();
        for(Entity entity : getEntities()) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(players.contains(player)) {
                    entityHider.showEntity(player, entity);
                } else {
                    entityHider.hideEntity(player, entity);
                }
            }
        }
    }

    public void announce(String s) {
        for(Player p : getAllPlayers()) {
            p.sendMessage(Colors.get(s));
        }
    }

    public void staffAnnounce(String s) {
        for(Player p : getAllPlayers()) {
            if(p.hasPermission("practice.staff")) {
                p.sendMessage(Colors.get(s));
            }
        }
    }
}
