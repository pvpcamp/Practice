package camp.pvp.practice.games;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.cooldowns.PlayerCooldown;
import camp.pvp.practice.games.impl.Duel;
import camp.pvp.practice.games.tournaments.Tournament;
import camp.pvp.practice.kits.CustomGameKit;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.profiles.PreviousQueue;
import camp.pvp.practice.profiles.Rematch;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.utils.BukkitReflection;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.EntityHider;
import camp.pvp.practice.Practice;
import camp.pvp.practice.utils.PlayerUtils;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.cooldown.CooldownModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter @Setter
public abstract class Game {

    public enum State {
        INACTIVE, STARTING, NEXT_ROUND_STARTING, ACTIVE, ROUND_ENDED, ENDED;
    }

    private final Practice plugin;
    private EntityHider entityHider;

    private final UUID uuid;
    private Map<UUID, GameParticipant> participants;
    private Map<UUID, GameSpectator> spectators;

    private List<Party> parties;
    private Tournament tournament;

    private State state;
    private Arena arena;
    private GameKit kit;

    public int round, timer;
    private Date created, started, ended;

    private BukkitTask startingTimer, endingTimer, shutdownTask;

    private List<Entity> entities;

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

    public abstract void initialize();

    public void startingTimer(int delay) {
        startingTimer = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int timer = delay;
            @Override
            public void run() {
                if(timer == 0) {
                    start();
                    startingTimer.cancel();
                } else {
                    if(timer == 30 || timer == 15 || timer == 10 || timer <= 5) {
                        announce("&aGame starting in &f" + timer + " &asecond" + (timer == 1 ? "" : "s") + ".");
                    }
                    timer--;
                }
            }
        }, 20, 20);
    }

    public void start() {
        for(Player p : getAllPlayers()) {
            if(p != null) {
                p.removePotionEffect(PotionEffectType.JUMP);
                p.playSound(p.getLocation(), Sound.FIREWORK_BLAST, 1, 1);
                p.sendMessage(ChatColor.GREEN + "The game has started.");
            }
        }

        setStarted(new Date());
        setState(Game.State.ACTIVE);
    }

    public abstract void end();

    /***
     * Does the final shutdown/cleanup of the game.
     * @param delay The delay in seconds before the game is shutdown.
     */
    public void cleanup(int delay) {
        shutdownTask = Bukkit.getScheduler().runTaskLater(plugin, ()-> {
            clearEntities();

            for(Map.Entry<UUID, GameParticipant> entry : getParticipants().entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                GameParticipant participant = entry.getValue();
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(entry.getKey());

                if(player != null) {
                    if(this instanceof Duel duel) {
                        GameQueue.Type queueType = duel.getQueueType();
                        if(queueType.equals(GameQueue.Type.UNRANKED) || queueType.equals(GameQueue.Type.RANKED) || queueType.equals(GameQueue.Type.PRIVATE)) {
                            PreviousQueue previousQueue = new PreviousQueue(duel.getKit(), queueType.equals(GameQueue.Type.PRIVATE) ? GameQueue.Type.UNRANKED : queueType);
                            profile.setPreviousQueue(previousQueue);

                            Rematch rematch;
                            for(GameParticipant p : getParticipants().values()) {
                                if(p.getUuid() != participant.getUuid() && p.getPlayer() != null && p.getPlayer().isOnline()) {
                                    rematch = new Rematch(profile, p.getUuid(), p.getName(), duel.getKit());
                                    profile.setRematch(rematch);
                                }
                            }
                        }
                    }

                    if(entry.getValue().isCurrentlyPlaying()) {

                        for(PlayerCooldown cooldown : participant.getCooldowns().values()) {
                            cooldown.remove();
                        }

                        if(participant.getRespawnTask() != null) participant.getRespawnTask().cancel();

                        if(Apollo.getPlayerManager().hasSupport(player.getUniqueId())) {

                            CooldownModule cooldownModule = Apollo.getModuleManager().getModule(CooldownModule.class);

                            Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
                            apolloPlayer.ifPresent(cooldownModule::resetCooldowns);

                        }

                        profile.setGame(null);
                        profile.playerUpdate(true);
                    }
                }
            }

            for(GameSpectator spectator : new ArrayList<>(getSpectators().values())) {
                Player player = Bukkit.getPlayer(spectator.getUuid());
                spectateEnd(player, true);
            }

            plugin.getGameProfileManager().updateGlobalPlayerVisibility();

            if(getArena() != null) getArena().resetArena();

            setState(State.INACTIVE);

            plugin.getGameManager().getGames().remove(getUuid());
        }, delay * 20L);
    }

    public void forceEnd(boolean resetPlayers) {
        this.announce("&c&lThis match has been forcefully ended by the server.");

        if(getStartingTimer() != null) getStartingTimer().cancel();
        if(getEndingTimer() != null) getEndingTimer().cancel();
        if(getShutdownTask() != null) getShutdownTask().cancel();

        if(resetPlayers) {

            for (Map.Entry<UUID, GameParticipant> entry : getParticipants().entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                GameParticipant participant = entry.getValue();
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(entry.getKey());

                if (player != null) {
                    if (entry.getValue().isAlive()) {
                        participant.clearCooldowns();

                        profile.setGame(null);
                        profile.playerUpdate(true);
                    }
                }
            }

            for(GameSpectator spectator : new ArrayList<>(getSpectators().values())) {
                spectateEnd(spectator.getPlayer(), true);
            }
        }

        clearEntities();
        setEnded(new Date());
        setState(State.ENDED);

        arena.resetArena();
    }

    private void killRespawn(Player player, GameParticipant participant) {
        PlayerUtils.reset(player, true);

        announce("&f" + player.getName() + "&a was killed" + (participant.getAttacker() == null ? "." : " by &f" + Bukkit.getOfflinePlayer(participant.getAttacker()).getName() + "&a."));

        PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false);
        player.addPotionEffect(invisibility);

        participant.respawn(3);
    }

    public void eliminate(Player player, boolean leftGame) {
        GameParticipant participant = getParticipants().get(player.getUniqueId());
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(participant == null || this.getState().equals(State.ENDED)) return;

        if(!participant.isAlive()) {
            if(leftGame) {
                participant.setLivingState(GameParticipant.LivingState.DEAD);

                if(participant.getRespawnTask() != null) participant.getRespawnTask().cancel();
            }

            return;
        }

        Location location = player.getLocation();

        boolean velocity = participant.getLastDamageCause() != null && participant.getLastDamageCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK);

        profile.getDeathAnimation().playAnimation(this, player, location, velocity);

        participant.kill();

        plugin.getGameProfileManager().updateGlobalPlayerVisibility();

        PostGameInventory pgi = new PostGameInventory(UUID.randomUUID(), participant, player.getInventory().getContents(), player.getInventory().getArmorContents());
        participant.setPostGameInventory(pgi);
        getPlugin().getGameManager().getPostGameInventories().put(pgi.getUuid(), pgi);

        if(getAlive().size() > 1 && kit.isDropItemsOnDeath()) {
            for (ItemStack item : player.getInventory()) {
                if (item != null && !item.getType().equals(Material.AIR)) {
                    Item i = player.getWorld().dropItem(player.getLocation(), item);
                    this.addEntity(i);
                }
            }
        }

        if(participant.isRespawn() && !leftGame) {
            killRespawn(player, participant);
            return;
        }

        if(leftGame) {
            participant.setLivingState(GameParticipant.LivingState.DEAD);
        } else {
            spectateStart(player);
            announce("&f" + player.getName() + "&a has been eliminated" + (participant.getAttacker() == null ? "." : " by &f" + Bukkit.getOfflinePlayer(participant.getAttacker()).getName() + "&a."));
        }
    }

    public boolean handleDamage(Player victim, EntityDamageEvent event) {
        GameParticipant participant = this.getParticipants().get(victim.getUniqueId());
        participant.setLastDamageCause(event.getCause());
        if(this.getState().equals(State.ACTIVE)) {
            double damage = event.getFinalDamage();
            boolean canDie = true;

            if(participant.isInvincible()) {
                event.setCancelled(true);
                return false;
            }

            int currentTick = plugin.getTickNumberCounter().getCurrentTick();

            if(participant.getLastInvalidHitTick() == currentTick && participant.getLastValidHitTick() != currentTick && event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                event.setCancelled(true);
                return false;
            }

            if(event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK)) {
                damage += 1;
            }

            if(!kit.isFallDamage() && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                event.setCancelled(true);
                canDie = false;
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
                    return true;
                }
            }
        } else {
            event.setCancelled(true);
        }

        return false;
    }

    public void handleHit(Player victim, Player attacker, EntityDamageByEntityEvent event) {
        GameParticipant participant = this.getCurrentPlaying().get(attacker.getUniqueId());
        GameParticipant victimParticipant = this.getCurrentPlaying().get(victim.getUniqueId());

        int currentTick = plugin.getTickNumberCounter().getCurrentTick();

        if(participant == null || victimParticipant == null) {
            event.setCancelled(true);
            return;
        }

        if(!getState().equals(State.ACTIVE)) event.setCancelled(true);

        if(!participant.isAlive() || !victimParticipant.isAlive()) event.setCancelled(true);

        if(event.isCancelled()) {
            victimParticipant.setLastInvalidHitTick(currentTick);
            return;
        }

        if(event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if(arrow.getShooter() instanceof Player) {
                attacker = (Player) arrow.getShooter();

                if(attacker != victim && getKit().isArrowOneShot()) {
                    event.setDamage(100);
                }

                if(attacker != victim && getKit().isShowArrowDamage()) {
                    int distance = (int) Math.round(attacker.getLocation().distance(victim.getLocation()));

                    if(victim.getHealth() - event.getFinalDamage() > 0) {
                        attacker.sendMessage(Colors.get("&f" + victim.getName() + " &6is now at &c" + Math.round(victim.getHealth() - event.getFinalDamage()) + " ❤ &7(" + distance + " blocks)"));
                    } else {
                        attacker.sendMessage(Colors.get("&6You shot and killed &f" + victim.getName() + " &6from &f" + distance + " blocks &6away!"));
                    }
                }
            }
        }

        if(participant.getAttacking() != null && !participant.getAttacking().equals(victim.getUniqueId())) {
            participant.setCurrentCombo(0);
        }

        participant.setHealth(Math.round(attacker.getHealth()));
        participant.setMaxHealth(Math.round(attacker.getMaxHealth()));
        participant.setHunger(attacker.getFoodLevel());
        participant.setAttacking(victim.getUniqueId());

        victimParticipant.setLastValidHitTick(currentTick);
        victimParticipant.setAttacker(attacker.getUniqueId());
        victimParticipant.setHealth(Math.round(victim.getHealth()));
        victimParticipant.setMaxHealth(Math.round(victim.getMaxHealth()));
        victimParticipant.setHunger(victim.getFoodLevel());
        victimParticipant.setPotionEffects(new ArrayList<>(victim.getActivePotionEffects()));

        if(event.getDamager() instanceof Player) {
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

        if (participant.currentCombo > participant.longestCombo) {
            participant.longestCombo = participant.currentCombo;
        }

        if(kit != null) {
            if (!kit.isTakeDamage()) {
                event.setDamage(0);
                victim.setHealth(victim.getMaxHealth());
                if (kit.equals(GameKit.BOXING) && participant.getHits() > 99) {
                    this.eliminate(victim, false);
                }
            }
        }
    }

    public void handleInteract(Player player, PlayerInteractEvent event) {
        GameParticipant participant = getCurrentPlaying().get(player.getUniqueId());
        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();

        if(participant == null) {
            event.setCancelled(true);
            return;
        }

        if(participant.isKitApplied()) {
            switch(player.getItemInHand().getType()) {
                case MUSHROOM_SOUP:
                    if(player.getHealth() != player.getMaxHealth()) {
                        double d = player.getMaxHealth() - player.getHealth() < 7 ? player.getMaxHealth() - player.getHealth() : 7;
                        player.setHealth(player.getHealth() + d);
                        player.setFoodLevel(20);
                        player.setSaturation(20);
                        item.setType(Material.BOWL);
                        event.setCancelled(true);
                    }
                    break;
                default:
                    handleRightClickedItem(player, item, event);
            }
        } else {
            GameKit kit = getKit();
            switch(player.getItemInHand().getType()) {
                case ENCHANTED_BOOK:
                    int slot = player.getInventory().getHeldItemSlot() + 1;
                    CustomGameKit cdk = participant.getProfile().getCustomDuelKits().get(kit).get(slot);
                    if(cdk != null) {
                        cdk.apply(participant);
                        participant.setAppliedCustomKit(cdk);
                        player.updateInventory();
                    }
                    break;
                case BOOK:
                    kit.apply(participant);
                    player.updateInventory();
                    break;
            }
        }

        if(getKit().isIssueCooldowns()) {
            switch (player.getItemInHand().getType()) {
                case ENDER_PEARL:
                    PlayerCooldown cooldown = participant.getCooldowns().get(PlayerCooldown.Type.ENDER_PEARL);
                    if (cooldown != null) {
                        if (!cooldown.isExpired()) {
                            player.sendMessage(cooldown.getBlockedMessage());
                            event.setCancelled(true);
                            player.updateInventory();
                        }
                    }
                    break;
            }
        }

        if(block != null) {

            if(!getState().equals(State.ACTIVE)) {
                event.setCancelled(true);
                return;
            }

            if(arena.getType().canModifyArena()) return;

            Material material = block.getType();

            if (material.equals(Material.CHEST) ||
                    material.equals(Material.TRAPPED_CHEST) ||
                    material.equals(Material.ENDER_CHEST) ||
                    material.equals(Material.FURNACE) ||
                    material.equals(Material.BURNING_FURNACE) ||
                    material.equals(Material.DISPENSER) ||
                    material.equals(Material.DROPPER) ||
                    material.equals(Material.HOPPER) ||
                    material.equals(Material.BREWING_STAND) ||
                    material.equals(Material.BEACON) ||
                    material.equals(Material.ANVIL) ||
                    material.equals(Material.ENDER_PORTAL_FRAME) ||
                    material.equals(Material.ENDER_PORTAL) ||
                    material.equals(Material.BED_BLOCK) ||
                    material.equals(Material.TRAP_DOOR) ||
                    material.equals(Material.WOODEN_DOOR) ||
                    material.equals(Material.WOOD_DOOR) ||
                    material.equals(Material.NOTE_BLOCK) ||
                    material.equals(Material.JUKEBOX) ||
                    material.equals(Material.CAKE_BLOCK) ||
                    material.equals(Material.WORKBENCH) ||
                    material.equals(Material.WOOD_BUTTON) ||
                    material.equals(Material.STONE_BUTTON)
            )
            {
                event.setCancelled(true);
            }
        }
    }

    public void handleRightClickedItem(Player player, ItemStack item, PlayerInteractEvent event) {

        if(!getState().equals(State.ACTIVE)) return;

        if(item.getType().equals(Material.FIREBALL) && getArena().getType().isBuild()) {
            Fireball fireball = player.launchProjectile(Fireball.class);
            fireball.setIsIncendiary(false);
            fireball.setVelocity(fireball.getDirection().multiply(2.5));
            addEntity(fireball);

            player.getInventory().removeItem(new ItemStack(Material.FIREBALL, 1));
            event.setCancelled(true);
        }
    }

    public GameParticipant join(Player player) {
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(profile.getGame() != null) {
            profile.getGame().leave(player);
        }

        profile.setGame(this);
        profile.setRematch(null);
        profile.getDuelRequests().clear();
        plugin.getGameQueueManager().removeFromQueue(player);

        GameParticipant participant = new GameParticipant(player.getUniqueId(), player.getName());
        participant.setGame(this);
        participant.setComboMessages(profile.isComboMessages());
        participant.setGameKit(kit);

        if(kit.isRespawn()) {
            participant.setRespawn(true);
        }

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

        spectators.put(player.getUniqueId(), new GameSpectator(player.getUniqueId(), player.getName()));

        GameParticipant participant = participants.get(player.getUniqueId());

        if(participant == null) {

            sendSpectateStartMessage(player);

            String message = "&f" + player.getName() + "&6 has started spectating.";

            if(profile.isStaffMode()) {
                staffAnnounce("&7[Staff] " + message);
            } else {
                announce(message);
            }
        }

        profile.setGame(this);

        plugin.getGameProfileManager().updateGlobalPlayerVisibility();
        updateEntities();

        if(location != null) {
            player.teleport(location);
        }

        if(getAlive().size() < 2) {
            PlayerUtils.reset(player, true);
        } else {
            profile.givePlayerItems();
        }

        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void spectateEnd(Player player, boolean updateLocation) {
        GameProfile profile = plugin.getGameProfileManager().getLoadedProfiles().get(player.getUniqueId());

        if(!state.equals(State.ENDED)) {
            if (!participants.containsKey(player.getUniqueId())) {
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
        profile.playerUpdate(updateLocation);

        plugin.getGameProfileManager().updateGlobalPlayerVisibility();
        updateEntities();
    }

    public void leave(Player player) {
        if(getCurrentPlaying().containsKey(player.getUniqueId())) {
            announce("&f" + player.getName() + "&a disconnected.");
            eliminate(player, true);
        } else {
            spectateEnd(player, true);
        }
    }

    public void handleBlockBreak(Player player, Block block, BlockBreakEvent event) {

        final GameParticipant breaker = getParticipants().get(player.getUniqueId());
        final Arena.Type type = arena.getType();
        final Location location = block.getLocation();
        final Material material = block.getType();

        if(type.canModifyArena()) {
            if(type.getSpecificBlocks() != null) {
                if(!type.getSpecificBlocks().contains(block.getType())) {
                    event.setCancelled(true);
                    return;
                }
            }
        } else {
            if(arena.isOriginalBlock(location)) {
                event.setCancelled(true);
                return;
            }
        }

        if(block.getType().equals(Material.SNOW_BLOCK)) {
            player.getInventory().addItem(new ItemStack(Material.SNOW_BALL));
        } else {
            for (ItemStack item : block.getDrops()) {
                Item i = block.getLocation().getWorld().dropItemNaturally(block.getLocation(), item);
                addEntity(i);
            }
        }

        if(arena.getBuildLimit() < block.getLocation().getBlockY()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You have reached the build limit.");
            return;
        }

        if(!block.getType().equals(Material.BED_BLOCK)) block.setType(Material.AIR);

        if(!arena.getType().equals(Arena.Type.DUEL_BED_FIGHT)) return;

        if(!material.equals(Material.BED_BLOCK)) return;

        GameParticipant participant = null;
        GameTeam.Color color = null;
        ArenaPosition bedPosition = null;

        for(int x = block.getX() - 2; x < block.getX() + 2; x++) {
            for(int z = block.getZ() - 2; z < block.getZ() + 2; z++) {
                for(ArenaPosition arenaPosition : arena.getPositions().values()) {
                    if(arenaPosition.getPosition().contains("bed") &&
                            arenaPosition.getLocation().equals(new Location(block.getWorld(), x, block.getY(), z))) bedPosition = arenaPosition;
                }
            }
        }

        if(bedPosition == null) return;

        for(GameParticipant p : getParticipants().values()) {
            if(bedPosition.getPosition().contains(p.getTeamColor().getName().toLowerCase())) {
                color = p.getTeamColor();
                participant = p;
            }
        }

        if(participant == null) return;

        if(breaker.getTeamColor().equals(color)) {
            player.sendMessage(ChatColor.RED + "You cannot destroy your own bed.");
            event.setCancelled(true);
            return;
        }

        block.setType(Material.AIR);

        participant.setRespawn(false);
        announceAll(
                " ",
                color.getChatColor() + "&l" + color.name() + " BED DESTROYED!",
                color.getChatColor() + "Bed has been destroyed by &f" + player.getName() + color.getChatColor() + "!",
                " ");

        playSound(null, Sound.ENDERDRAGON_GROWL, 1F, 1F);

        if(participant.getTeam() != null) participant.getTeam().setRespawn(false);
    }

    public boolean isBuild() {
        return kit.isBuild();
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>(getCurrentPlayersPlaying());
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
        Map<UUID, GameParticipant> playing = new HashMap<>();

        for(Map.Entry<UUID, GameParticipant> entry : participants.entrySet()) {
            if(entry.getValue().isCurrentlyPlaying()) {
                playing.put(entry.getKey(), entry.getValue());
            }
        }
        return playing;
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
        i += getCurrentPlaying().size();
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
            player.playSound(location == null ? player.getLocation() : location, sound, v1, v2);
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

    public Location getRespawnLocation(GameParticipant participant) {
        if(getArena().getType().isRandomSpawnLocation()) {
            Map<Location, Double> distances = new HashMap<>();

            List<Location> locations = new ArrayList<>(getArena().getRandomSpawnLocations());
            Collections.shuffle(locations);

            for(Location l : locations) {
                double nearestDistance = Double.MAX_VALUE;
                for(Player player : getAlivePlayers()) {
                    double distance = player.getLocation().distance(l);
                    if(distance < nearestDistance) {
                        nearestDistance = distance;
                    }
                }

                distances.put(l, nearestDistance);
            }

            for(Map.Entry<Location, Double> entry : distances.entrySet()) {
                if(Objects.equals(entry.getValue(), Collections.max(distances.values()))) {
                    return entry.getKey();
                }
            }
        }

        return participant.getSpawnLocation();
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

    public String getScoreboardTitle() {
        return "&fGame";
    }
}
