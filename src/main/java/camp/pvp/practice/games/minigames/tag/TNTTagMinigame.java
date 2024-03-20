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
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TNTTagMinigame extends Minigame {

    @Getter @Setter private int round, timer;
    private BukkitTask roundTimerTask;

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
                lines.add("&6Players: &f" + this.getCurrentPlaying().size());
                break;
            case ACTIVE:
                lines.add("&6Round: &f" + round);

                if(timer > -1) {
                    lines.add("&6Time Left: &f" + timer + "s");
                }

                lines.add("&6Alive: &f" + this.getCurrentPlaying().size() + "/" + this.getParticipants().size());
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

                if(self.isTagged()) {
                    lines.add(" ");
                    lines.add("&c&lYOU ARE TAGGED!");
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
                lines.add("&6Round: &f" + round);

                if(timer > -1) {
                    lines.add("&6Time Left: &f" + timer + "s");
                }

                lines.add("&6Alive: &f" + this.getCurrentPlaying().size() + "/" + this.getParticipants().size());
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

        if(timer == 0) return;

        TNTTagParticipant victimParticipant = (TNTTagParticipant) getParticipants().get(victim.getUniqueId()),
                attackerParticipant = (TNTTagParticipant) getParticipants().get(attacker.getUniqueId());

        if(attackerParticipant.isTagged()) {
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

        for(PotionEffect effect : victimPlayer.getActivePotionEffects()){
            victimPlayer.removePotionEffect(effect.getType());
        }

        PotionEffect speedThree = new PotionEffect(PotionEffectType.SPEED, 999999, 2, true, false);
        victimPlayer.addPotionEffect(speedThree);

        for(int i = 0; i < 9; i++) {
            victimInventory.setItem(i, tnt);
        }

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
        timer = 20;
        if(size > 10) timer = 45;
        else if(size > 5) timer = 30;

        for(GameParticipant participant : getAlive().values()) {
            TNTTagParticipant tntTagParticipant = (TNTTagParticipant) participant;
            tntTagParticipant.setTagged(false);

            Player player = participant.getPlayer();

            PotionEffect speedTwo = new PotionEffect(PotionEffectType.SPEED, 999999, 1, true, false);
            player.addPotionEffect(speedTwo);
        }

        for(TNTTagParticipant participant : selectRandomAlive()) {
            tag(participant);
        }

        announce("""
                
                &6&lRound %s
                 &7● &6Time: &f%ss
                 &7● &6Tagged: &f%s
                 \n
                """.formatted(round, timer, getTagged().size()));

        playSound(null, Sound.FIREWORK_LAUNCH, 1.0F, 1.0F);

        roundTimerTask = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            if(timer < 1) {
                roundTimerTask.cancel();

                timer = Integer.MIN_VALUE;

                for(TNTTagParticipant participant : getTagged()) {
                    eliminate(participant.getPlayer(), false);
                }

                Bukkit.getScheduler().runTaskLater(getPlugin(), this::nextRound, 60L);
                return;
            }

            timer--;
        }, 20, 20);
    }

    @Override
    public void eliminate(Player player, boolean leftGame) {
        super.eliminate(player, leftGame, false);

        TNTTagParticipant participant = (TNTTagParticipant) getParticipants().get(player.getUniqueId());
        participant.setTagged(false);

        playEffect(player.getLocation(), Effect.EXPLOSION_HUGE, null);
        playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);

        if(getAlive().size() < 2) {
            end();
        }
    }

    @Override
    public void sendEndingMessage() {
        StringBuilder topPlayers = new StringBuilder();
        List<GameParticipant> sortedParticipants = new ArrayList<>(this.getParticipants().values());

        sortedParticipants.forEach(p -> {
            if(p.getAliveTime() == null) p.setAliveTime(new Date());
        });

        sortedParticipants.sort((p1, p2) -> Integer.compare(p2.getKills(), p1.getKills()));

        for(int i = 0; i < Math.min(sortedParticipants.size(), 3); i++) {
            GameParticipant p = sortedParticipants.get(i);
            String aliveTime = p.getDeathTime() == null ? "&6&lALIVE" : TimeUtil.get(p.getDeathTime(), p.getAliveTime());
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

        if(size > 10) amount = 2;
        else if(size > 5) amount = 3;

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
