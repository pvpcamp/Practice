package camp.pvp.practice.games;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.games.tournaments.Tournament;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.BukkitReflection;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.EntityHider;
import camp.pvp.practice.Practice;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter @Setter
public abstract class Game {

    public enum State {
        INACTIVE, STARTING, NEXT_ROUND_STARTING, ACTIVE, ROUND_ENDED, ENDED;
    }

    private final Practice plugin;
    private EntityHider entityHider;

    public final UUID uuid;
    public Map<UUID, GameParticipant> participants;
    public Map<UUID, GameSpectator> spectators;

    private List<Party> parties;
    private Tournament tournament;

    public State state;
    public Arena arena;
    public DuelKit kit;

    public int round, timer;
    public Date created, started, ended;

    public BukkitTask startingTimer, endingTimer;

    public List<Entity> entities;

    protected Game(Practice plugin, UUID uuid) {
        this.plugin = plugin;
        this.entityHider = plugin.getEntityHider();
        this.uuid = uuid;
        this.parties = new ArrayList<>();
        this.participants = new HashMap<>();
        this.spectators = new HashMap<>();
        this.entities = new ArrayList<>();
        this.state = State.INACTIVE;
        this.round = 0;

        this.plugin.getGameManager().addGame(this);
    }

    public abstract List<String> getScoreboard(GameProfile profile);

    public abstract List<String> getSpectatorScoreboard(GameProfile profile);

    public abstract void start();

    public abstract void end();

    public void forceEnd() {
        this.announce("&c&lThis match has been forcefully ended by the server.");

        if(getStartingTimer() != null) {
            getStartingTimer().cancel();
        }

        if(getEndingTimer() != null) {
            getEndingTimer().cancel();
        }

        arena.resetArena();

        for(Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            GameParticipant participant = entry.getValue();
            GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(entry.getKey());

            if(player != null) {
                if(entry.getValue().isAlive()) {
                    participant.clearCooldowns();

                    profile.setGame(null);
                    profile.playerUpdate(true);
                }
            }
        }

        for(GameSpectator spectator : new ArrayList<>(this.getSpectators().values())) {
            Player player = Bukkit.getPlayer(spectator.getUuid());
            this.spectateEnd(player);
        }

        this.clearEntities();
        this.setEnded(new Date());
        this.setState(State.ENDED);
    }

    public void eliminate(Player player, boolean leftGame) {
        GameParticipant participant = getParticipants().get(player.getUniqueId());
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());
        if(participant != null && !this.getState().equals(State.ENDED)) {
            participant.eliminate();
            participant.clearCooldowns();

            PostGameInventory pgi = new PostGameInventory(UUID.randomUUID(), participant, player.getInventory().getContents(), player.getInventory().getArmorContents());
            participant.setPostGameInventory(pgi);
            getPlugin().getGameManager().getPostGameInventories().put(pgi.getUuid(), pgi);

            if(getAlive().size() > 1) {
                for (ItemStack item : player.getInventory()) {
                    if (item != null && !item.getType().equals(Material.AIR)) {
                        Item i = player.getWorld().dropItem(player.getLocation(), item);
                        this.addEntity(i);
                    }
                }
            }

            if(leftGame) {
                Game.this.announce("&f" + player.getName() + "&a disconnected.");
            } else {
                Game.this.spectateStart(player);
                Game.this.announce("&f" + player.getName() + "&a has been eliminated" + (participant.getAttacker() == null ? "." : " by &f" + Bukkit.getOfflinePlayer(participant.getAttacker()).getName() + "&a."));
            }

            plugin.getGameProfileManager().updateGlobalPlayerVisibility();

            Location location = player.getLocation();

            if(participant.getLastDamageCause() != null && participant.getLastDamageCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                GameProfile attackerProfile = plugin.getGameProfileManager().getLoadedProfiles().get(participant.getAttacker());
                if(attackerProfile != null) {
                    attackerProfile.getDeathAnimation().playAnimation(this, player, location, true);
                }
            } else {
                profile.getDeathAnimation().playAnimation(this, player, location, false);
            }
        }
    }

    public void handleDamage(Player victim, EntityDamageEvent event) {
        GameParticipant participant = this.getParticipants().get(victim.getUniqueId());
        participant.setLastDamageCause(event.getCause());
        if(this.getState().equals(State.ACTIVE)) {
            double damage = event.getFinalDamage();
            boolean canDie = true;

            if(event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
                damage += 1;
            }

            if(kit != null && !kit.isTakeDamage()) {
                if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                    event.setCancelled(true);
                    canDie = false;
                }

                event.setDamage(0);
                victim.setHealth(victim.getMaxHealth());
            }

            if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) && victim.getNoDamageTicks() > 9) {
                canDie = false;
            }

            if(participant.isArcherTagged()) {
                event.setDamage(event.getDamage() * 1.25);
            }

            participant.setHealth(Math.round(victim.getHealth()));
            participant.setMaxHealth(Math.round(victim.getMaxHealth()));
            participant.setHunger(victim.getFoodLevel());
            participant.setPotionEffects(new ArrayList<>(victim.getActivePotionEffects()));

            if(event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                participant.setCurrentCombo(0);
            }

            if(victim.getHealth() - damage < 0) {
                event.setCancelled(true);
                if(canDie && getState().equals(State.ACTIVE)) {
                    this.eliminate(victim, false);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> victim.setHealth(20), 1);
                }
            }
        } else {
            event.setCancelled(true);
        }
    }

    public void handleHit(Player victim, Player attacker, EntityDamageByEntityEvent event) {
        GameParticipant victimParticipant = this.getCurrentPlaying().get(victim.getUniqueId());
        GameParticipant participant = this.getCurrentPlaying().get(attacker.getUniqueId());
        if(victimParticipant != null && participant != null && getState().equals(State.ACTIVE)) {
            if(victimParticipant.isAlive() && participant.isAlive()) {

                if(participant.getAttacking() != null && !participant.getAttacking().equals(victim.getUniqueId())) {
                    participant.setCurrentCombo(0);
                }

                participant.setHealth(Math.round(attacker.getHealth()));
                participant.setMaxHealth(Math.round(attacker.getMaxHealth()));
                participant.setHunger(attacker.getFoodLevel());
                participant.setAttacking(victim.getUniqueId());

                victimParticipant.setAttacker(attacker.getUniqueId());

                victimParticipant.setHealth(Math.round(victim.getHealth()));
                victimParticipant.setMaxHealth(Math.round(victim.getMaxHealth()));
                victimParticipant.setHunger(victim.getFoodLevel());
                victimParticipant.setPotionEffects(new ArrayList<>(victim.getActivePotionEffects()));

                if(event.getDamager() instanceof Player) {
                    if (victim.getNoDamageTicks() <= 10) {
                        participant.hits++;
                        participant.currentCombo++;

                        if (participant.isComboMessages()) {
                            switch ((int) participant.getCurrentCombo()) {
                                case 5:
                                    attacker.playSound(attacker.getLocation(), Sound.FIREWORK_LAUNCH, 1F, 1F);
                                    attacker.sendMessage(Colors.get("&a ** 5 Hit Combo! **"));
                                    break;
                                case 10:
                                    attacker.playSound(attacker.getLocation(), Sound.EXPLODE, 1F, 1F);
                                    attacker.sendMessage(Colors.get("&6&o ** 10 HIT COMBO! **"));
                                    break;
                                case 20:
                                    attacker.playSound(attacker.getLocation(), Sound.ENDERDRAGON_GROWL, 1F, 1F);
                                    attacker.sendMessage(Colors.get("&4&l&o ** 20 HIT COMBO!!! **"));
                                    break;
                            }
                        }
                    }
                }

                if(kit != null) {
                    if (!kit.isTakeDamage()) {
                        event.setDamage(0);
                        victim.setHealth(victim.getMaxHealth());
                        if (kit.equals(DuelKit.BOXING) && participant.getHits() > 99) {
                            this.eliminate(victim, false);
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

    public GameParticipant join(Player player) {
        GameProfile profile = plugin.getGameProfileManager().find(player.getUniqueId(), true);

        if(profile.getGame() != null) {
            profile.getGame().leave(player);
        }

        profile.setGame(this);
        profile.getDuelRequests().clear();
        plugin.getGameQueueManager().removeFromQueue(player);

        GameParticipant participant = new GameParticipant(player.getUniqueId(), player.getName());
        participant.setComboMessages(profile.isComboMessages());

        profile.updatePlayerVisibility();

        this.participants.put(player.getUniqueId(), participant);

        return participant;
    }

    public void spectateStart(Player player) {
        spectateStart(player, null);
    }

    public void spectateStartRandom(Player player) {
        Location location = null;
        if(this.getAlive().size() > 0) {
            location = this.getAlivePlayers().get(0).getLocation();
        }

        spectateStart(player, location);
    }

    public void spectateStart(Player player, Location location) {
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(profile.getGame() != null && !profile.getGame().equals(this)) {
            profile.getGame().leave(player);
        }

        this.getSpectators().put(player.getUniqueId(), new GameSpectator(player.getUniqueId(), player.getName()));

        if(!this.getParticipants().containsKey(player.getUniqueId())) {

            sendSpectateStartMessage(player);

            String message = "&f" + player.getName() + "&6 has started spectating.";

            if(profile.isStaffMode()) {
                this.staffAnnounce("&7[Staff] " + message);
            } else {
                this.announce(message);
            }
        }

        profile.setGame(this);

        plugin.getGameProfileManager().updateGlobalPlayerVisibility();
        updateEntities();

        if(location != null) {
            player.teleport(location);
        }

        profile.givePlayerItems();

        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void spectateEnd(Player player) {
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(!this.getState().equals(State.ENDED)) {
            if (!this.getParticipants().containsKey(player.getUniqueId())) {
                String message = "&f" + player.getName() + "&6 has stopped spectating.";
                if (profile.isStaffMode()) {
                    this.staffAnnounce("&7[Staff] " + message);
                } else {
                    this.announce(message);
                }
            }
        }

        this.getSpectators().remove(player.getUniqueId());

        profile.setGame(null);
        profile.playerUpdate(true);

        plugin.getGameProfileManager().updateGlobalPlayerVisibility();
        updateEntities();
    }

    public void leave(Player player) {
        if(getAlive().containsKey(player.getUniqueId())) {
            eliminate(player, true);
        } else {
            spectateEnd(player);
        }
    }

    public boolean isBuild() {
        return kit.isBuild();
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

    public Map<UUID, GameParticipant> getCurrentPlaying() {
        return this.getAlive();
    }

    public List<Player> getCurrentPlayersPlaying() {
        List<Player> players = new ArrayList<>();
        for(UUID uuid : getCurrentPlaying().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                players.add(player);
            }
        }
        return players;
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

    public int countAll() {
        int i = 0;
        i += getAlive().size();
        i += getSpectators().size();
        return i;
    }

    public boolean seeEveryone() {
        return false;
    }

    public void sendSpectateStartMessage(Player player) {
        player.sendMessage(ChatColor.GREEN + "You have started spectating.");
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

    public void playLightning(Location location) {
        for(Player player : getAllPlayers()) {
            BukkitReflection.sendLightning(player, location);
        }

        for(int x = location.getBlockX() - 2; x < location.getBlockX() + 2; x++) {
            for(int y = location.getBlockY() - 2; y < location.getBlockY() + 2; y++) {
                for(int z = location.getBlockZ() - 2; z < location.getBlockZ() + 2; z++) {
                    Location l = new Location(location.getWorld(), x, y, z);
                    Block block = l.getBlock();
                    if (block != null) {
                        Block fire = block.getRelative(BlockFace.UP);
                        if (fire != null && fire.getType().equals(Material.FIRE)) {
                            fire.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }

    public void handleBorder(Player player) {
        ArenaPosition corner1 = arena.getPositions().get("corner1");
        ArenaPosition corner2 = arena.getPositions().get("corner2");

        if (corner1 != null && corner2 != null) {
            Location location, c1, c2;
            location = player.getLocation();
            c1 = corner1.getLocation();
            c2 = corner2.getLocation();
            int x, z, minX, minZ, maxX, maxZ;
            x = location.getBlockX();
            z = location.getBlockZ();
            minX = Math.min(c1.getBlockX(), c2.getBlockX());
            minZ = Math.min(c1.getBlockZ(), c2.getBlockZ());
            maxX = Math.max(c1.getBlockX(), c2.getBlockX());
            maxZ = Math.max(c1.getBlockZ(), c2.getBlockZ());

            boolean reachedBorder = false;
            if(minX > x) {
                reachedBorder = true;
                location.add(1, 0, 0);
            }

            if(maxX < x) {
                reachedBorder = true;
                location.subtract(1, 0, 0);
            }

            if(minZ > z) {
                reachedBorder = true;
                location.add(0, 0, 1);
            }

            if(maxZ < z) {
                reachedBorder = true;
                location.subtract(0, 0, 1);
            }

            if(reachedBorder) {
                player.teleport(location);
                player.sendMessage(ChatColor.RED + "You have reached the border of the arena.");
            }
        }
    }

    public boolean isInBorder(Location location) {
        ArenaPosition corner1 = arena.getPositions().get("corner1");
        ArenaPosition corner2 = arena.getPositions().get("corner2");

        if (corner1 != null && corner2 != null) {
            Location c1, c2;
            c1 = corner1.getLocation();
            c2 = corner2.getLocation();
            int x, z, minX, minZ, maxX, maxZ;
            x = location.getBlockX();
            z = location.getBlockZ();
            minX = Math.min(c1.getBlockX(), c2.getBlockX());
            minZ = Math.min(c1.getBlockZ(), c2.getBlockZ());
            maxX = Math.max(c1.getBlockX(), c2.getBlockX());
            maxZ = Math.max(c1.getBlockZ(), c2.getBlockZ());

            return minX <= x && maxX >= x && minZ <= z && maxZ >= z;
        }
        return true;
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

    public void clearEntities() {
        for(Entity entity : getEntities()) {
            entity.remove();
        }
    }

    public void announce(String s) {
        for(Player p : getAllPlayers()) {
            p.sendMessage(Colors.get(s));
        }
    }

    public void staffAnnounce(String s) {
        for (Player p : getAllPlayers()) {
            if (p.hasPermission("practice.staff")) {
                p.sendMessage(Colors.get(s));
            }
        }
    }

    public void announceAll(String... strings) {
        for(Player p : getAllPlayers()) {
            for(String s : strings) {
                p.sendMessage(Colors.get(s));
            }
        }
    }
}
