package camp.pvp.practice.games.minigames.tag;

import camp.pvp.core.utils.DateUtils;
import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.minigames.Minigame;
import camp.pvp.practice.games.tasks.TeleportFix;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TNTTagMinigame extends Minigame {

    @Getter @Setter private int round, timer;
    private BukkitTask roundTimerTask, compassUpdaterTask;

    public TNTTagMinigame(Practice plugin, UUID uuid) {
        super(plugin, uuid);

        setKit(GameKit.TNT_TAG.getBaseKit());
        setType(Type.TNT_TAG);
    }

    @Override
    public TNTTagParticipant createParticipant(Player player) {
        return new TNTTagParticipant(player.getUniqueId(), player.getName());
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        TNTTagParticipant self = (TNTTagParticipant) getParticipants().get(profile.getUuid());
        List<String> lines = new ArrayList<>();

        switch(getState()) {
            case STARTING:
                lines.add("&6Minigame: &fTNT Tag");
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                lines.add("&6Players: &f" + getCurrentPlaying().size());
                break;
            case ACTIVE:
                lines.add("&7Round " + round);

                if(timer > -1) {
                    lines.add(" ");
                    lines.add("&cExplosion in " + (timer < 3 ? "&4&l" : "&f") + (timer + 1) + "s");
                    lines.add("&aGoal: &f" + (self.isTagged() ? "Tag someone!" : "Run away!"));
                }

                lines.add(" ");

                lines.add("&6Alive: &f" + getCurrentPlaying().size() + "/" + getParticipants().size());
                lines.add("&6Tagged: &f" + getTagged().size());

                if(profile.isSidebarShowDuration() || profile.isSidebarShowPing()) {
                    lines.add(" ");
                }

                if(profile.isSidebarShowPing()) {
                    lines.add("&6Ping: &f" + PlayerUtils.getPing(profile.getPlayer()) + " ms");
                }

                if(profile.isSidebarShowDuration()) {
                    lines.add("&6Duration: &f" + TimeUtil.get(getStarted()));
                }


                break;
            case ENDED:
                lines.add("&6Winner: &f" + getWinner().getName());
                break;
        }

        return lines;
    }

    @Override
    public List<String> getSpectatorScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();

        switch(getState()) {
            case STARTING:
                lines.add("&6Minigame: &fTNT Tag");
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                lines.add("&6Players: &f" + this.getCurrentPlaying().size());
                break;
            case ACTIVE:
                lines.add("&7Round " + round);

                if(timer > -1) {
                    lines.add(" ");
                    lines.add("&cExplosion in " + (timer < 3 ? "&4&l" : "&f") + (timer + 1) + "s");
                }

                lines.add(" ");

                lines.add("&6Alive: &f" + getCurrentPlaying().size() + "/" + getParticipants().size());
                lines.add("&6Tagged: &f" + getTagged().size());

                if(profile.isSidebarShowDuration()) {
                    lines.add(" ");
                    lines.add("&6Duration: &f" + TimeUtil.get(getStarted()));
                }
                break;
            case ENDED:
                lines.add("&6Winner: &f" + getWinner().getName());
                break;
        }

        return lines;
    }

    @Override
    public void handleHit(Player victim, Player attacker, EntityDamageByEntityEvent event) {
        super.handleHit(victim, attacker, event);

        if(!getState().equals(State.ACTIVE)) return;

        if(!getAlive().containsKey(victim.getUniqueId()) || !getAlive().containsKey(attacker.getUniqueId())) return;

        if(timer < 0) return;

        TNTTagParticipant victimParticipant = (TNTTagParticipant) getParticipants().get(victim.getUniqueId()),
                attackerParticipant = (TNTTagParticipant) getParticipants().get(attacker.getUniqueId());

        if(attackerParticipant.isTagged() && !victimParticipant.isTagged()) {
            tag(victimParticipant, attackerParticipant);
        }
    }

    public void tag(TNTTagParticipant victim) {
        tag(victim, null);
    }

    public void tag(TNTTagParticipant victim, TNTTagParticipant attacker) {
        victim.setTagged(true);

        Player victimPlayer = victim.getPlayer();
        PlayerInventory victimInventory = victimPlayer.getInventory();

        ItemStack tnt = new ItemStack(Material.TNT);
        victimInventory.setHelmet(tnt);
        victimInventory.clear();

        victimPlayer.sendMessage(ChatColor.RED + "You have been tagged!");

        playSound(victimPlayer.getLocation(), Sound.FIREWORK_LAUNCH, 1.0F, 1.0F);

        for(PotionEffect effect : victimPlayer.getActivePotionEffects()){
            victimPlayer.removePotionEffect(effect.getType());
        }

        PotionEffect speedThree = new PotionEffect(PotionEffectType.SPEED, 999999, 3, true, false);
        victimPlayer.addPotionEffect(speedThree);

        victimInventory.setItem(0, new ItemStack(Material.TNT));

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(Colors.get("&6Player Tracker"));
        compass.setItemMeta(compassMeta);

        victimInventory.setItem(1, compass);

        if(attacker != null) {
            attacker.setTagged(false);

            Player attackerPlayer = attacker.getPlayer();
            PlayerInventory attackerInventory = attackerPlayer.getInventory();

            attackerInventory.setHelmet(null);
            attackerInventory.clear();

            for(PotionEffect effect : attackerPlayer.getActivePotionEffects()){
                attackerPlayer.removePotionEffect(effect.getType());
            }

            PotionEffect speedTwo = new PotionEffect(PotionEffectType.SPEED, 999999, 1, true, false);
            attackerPlayer.addPotionEffect(speedTwo);

            attackerPlayer.sendMessage(ChatColor.GREEN + "You have tagged " + ChatColor.WHITE + victimPlayer.getName() + ChatColor.GREEN + "!");
        }
    }

    @Override
    public void initialize() {
        if(getArena() == null) {
            setArena(getPlugin().getArenaManager().selectRandomArena(Arena.Type.MINIGAME_TNT_TAG));
        }

        if(getArena() == null) {
            for(Player p : getAlivePlayers()) {
                p.sendMessage(ChatColor.RED + "There are no arenas currently available for the ladder selected. Please notify a staff member.");
                cleanup(0);
            }
            return;
        }

        Arena arena = getArena();

        arena.prepare();

        this.setState(State.STARTING);

        sendStartingMessage();

        for(GameParticipant participant : getParticipants().values()) {
            Player p = Bukkit.getPlayer(participant.getUuid());

            ArenaPosition position = arena.getPositions().get("spawn");
            participant.setSpawnLocation(position.getLocation());

            p.teleport(position.getLocation());
            participant.getProfile().givePlayerItems();
        }

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

        Bukkit.getScheduler().runTaskLater(getPlugin(), new TeleportFix(this), 1);

        startingTimer(5);

        compassUpdaterTask = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            for(TNTTagParticipant participant : getTagged()) {
                Player player = participant.getPlayer();
                Location location = player.getLocation(), nearest = null;
                for(GameParticipant p : getAlive().values()) {
                    TNTTagParticipant target = (TNTTagParticipant) p;
                    if(p.getUuid().equals(participant.getUuid()) || target.isTagged()) continue;

                    Location l = p.getPlayer().getLocation();
                    if(nearest == null || location.distance(l) < location.distance(nearest)) {
                        nearest = l;
                    }
                }

                if(nearest != null) player.setCompassTarget(nearest);
            }
        }, 0, 5);
    }

    @Override
    public void start() {
        super.start();
        nextRound();
    }

    public void nextRound() {

        if(getState().equals(State.ENDED)) return;

        if(getAlive().size() < 2) {
            end();
            return;
        }

        if(roundTimerTask != null) roundTimerTask.cancel();

        round++;

        final int size = getAlive().size();
        if(size > 12) timer = 45;
        else if(size > 5) {
            timer = 30;
        } else {
            timer = 20;
            for(GameParticipant participant : getAlive().values()) {
                participant.getPlayer().teleport(participant.getSpawnLocation());
            }
        }

        for(GameParticipant participant : getAlive().values()) {
            TNTTagParticipant tntTagParticipant = (TNTTagParticipant) participant;
            tntTagParticipant.setTagged(false);

            Player player = participant.getPlayer();

            PotionEffect speedTwo = new PotionEffect(PotionEffectType.SPEED, 999999, 1, true, false);
            player.addPotionEffect(speedTwo);
        }

        StringBuilder sb = new StringBuilder();
        List<TNTTagParticipant> random = selectRandomAlive();
        for(TNTTagParticipant participant : random) {
            tag(participant);

            sb.append("&f").append(participant.getName());

            if(random.indexOf(participant) != random.size() - 1) {
                sb.append("&7, ");
            }
        }

        announce("""
                
                &6&lRound %s
                 &7● &6Time: &f%ss
                 &7● &6Tagged: %s
                 \n
                """.formatted(round, timer, sb));

        roundTimerTask = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            if(timer < 1) {
                roundTimerTask.cancel();

                endRound();
                return;
            }

            if(timer == 3) {
                announce("&c&oThe fuse has been lit...");
                playSound(null, Sound.FUSE, 1.0F, 1.0F);
            }

            timer--;
        }, 20, 20);
    }

    public void endRound() {
        timer = Integer.MIN_VALUE;

        final List<TNTTagParticipant> tagged = new ArrayList<>(getTagged());

        StringBuilder sb = new StringBuilder();

        for(TNTTagParticipant participant : tagged) {
            eliminate(participant.getPlayer(), false, false, false);

            sb.append("&f").append(participant.getName());

            if(tagged.indexOf(participant) != tagged.size() - 1) {
                sb.append("&7, ");
            }
        }

        if(!getState().equals(State.ACTIVE)) return;

        if(tagged.size() > 1) {
            sb.append("&c have blown up!");
        } else {
            sb.append("&c has blown up!");
        }

        announce("""
                
                &4&lBOOM!
                %s
                 \n
                """.formatted(sb.toString())
        );

        roundTimerTask = Bukkit.getScheduler().runTaskLater(getPlugin(), this::nextRound, 60L);
    }

    @Override
    public void end() {
        super.end();
        timer = Integer.MIN_VALUE;
        if(roundTimerTask != null) roundTimerTask.cancel();
        compassUpdaterTask.cancel();
    }

    @Override
    public void eliminate(Player player, boolean leftGame, boolean showDeathAnimation, boolean showDeathMessage) {
        super.eliminate(player, leftGame, false, showDeathMessage);

        playEffect(player.getLocation(), Effect.EXPLOSION_HUGE, null);
        playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);

        TNTTagParticipant participant = (TNTTagParticipant) getParticipants().get(player.getUniqueId());

        if(participant.isTagged()) {
            if(getTagged().size() == 1 && timer > 0) {
                if(roundTimerTask != null) roundTimerTask.cancel();
                nextRound();
            }

            participant.setTagged(false);
        }

        if(getAlive().size() < 2) {
            end();
        }
    }

    @Override
    public void sendEndingMessage() {
        StringBuilder topPlayers = new StringBuilder();
        List<GameParticipant> sortedParticipants = new ArrayList<>(this.getParticipants().values());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 1);
        final Date now = calendar.getTime();

        sortedParticipants.forEach(p -> {
            if(p.getAliveTime() == null) p.setAliveTime(now);
            if(p.getDeathTime() == null) p.setDeathTime(now);

        });

        sortedParticipants.sort((p1, p2) -> p2.getDeathTime().compareTo(p1.getDeathTime()));

        for(int i = 0; i < Math.min(sortedParticipants.size(), 3); i++) {
            GameParticipant p = sortedParticipants.get(i);
            String aliveTime = p.getDeathTime() == now ? "&6&lALIVE" : TimeUtil.get(p.getDeathTime(), p.getAliveTime());
            topPlayers.append("\n &7● &6" + (i + 1) + ": &f" + p.getName() + " &7" + aliveTime);
        }

        String endMessage = """
                
                &6&lMinigame finished.
                 &7● &6Winner: &f%s
                 \n
                &6&lTop Players: %s
                 \n
                """.formatted(getWinner().getName(), topPlayers.toString());
        announce(endMessage);
    }


    private List<TNTTagParticipant> selectRandomAlive() {
        List<TNTTagParticipant> participants = new ArrayList<>(), selected = new ArrayList<>();

        for(GameParticipant participant : getAlive().values()) {
            TNTTagParticipant tntTagParticipant = (TNTTagParticipant) participant;
            if(!tntTagParticipant.isTagged()) participants.add(tntTagParticipant);
        }

        Collections.shuffle(participants);

        final int size = participants.size();
        int amount = 1;

        if(size > 12) amount = 3;
        else if(size > 5) amount = 2;

        for(int i = 0; i < amount; i++) {
            selected.add(participants.get(i));
        }

        return selected;
    }

    public List<TNTTagParticipant> getTagged() {
        List<TNTTagParticipant> tagged = new ArrayList<>();
        for(GameParticipant participant : getParticipants().values()) {
            TNTTagParticipant tntTagParticipant = (TNTTagParticipant) participant;
            if(tntTagParticipant.isTagged()) {
                tagged.add(tntTagParticipant);
            }
        }

        return tagged;
    }

    @Override
    public GameParticipant determineWinner() {
        GameParticipant participant = getAlive().values().stream().findFirst().orElse(null);
        setWinner(participant);
        return participant;
    }
}
