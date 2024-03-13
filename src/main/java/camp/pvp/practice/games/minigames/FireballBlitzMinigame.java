package camp.pvp.practice.games.minigames;

import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.GameTeam;
import camp.pvp.practice.games.tasks.TeleportFix;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FireballBlitzMinigame extends Minigame{

    public FireballBlitzMinigame(Practice plugin, UUID uuid) {
        super(plugin, uuid);

        setType(Type.FIREBALL_BLITZ);
        setKit(GameKit.FIREBALL_FIGHT.getBaseKit());
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        GameParticipant self = getParticipants().get(profile.getUuid());
        List<String> lines = new ArrayList<>();

        for(GameParticipant participant : getParticipants().values()) {
            GameTeam.Color color = participant.getTeamColor();

            StringBuilder sb = new StringBuilder();

            sb.append(color.getChatColor());
            sb.append(color.toString().charAt(0));
            sb.append("&l &f");
            sb.append(color.getName());
            sb.append(": ");
            sb.append(color.getChatColor()).append(ChatColor.BOLD);
            sb.append(participant.isRespawn() ? "✔" : (participant.isCurrentlyPlaying() ? "1" : "✘"));

            if(participant.equals(self)) {
                sb.append(" &7YOU");
            }

            lines.add(sb.toString());
        }

        lines.add(" ");

        switch(getState()) {
            case STARTING:
                lines.add("&6Minigame: &fFireball Blitz");
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                lines.add("&6Players: &f" + this.getCurrentPlaying().size());
                break;
            case ACTIVE:
                lines.add("&6Kills: &f" + self.getKills());

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

        for(GameParticipant participant : getParticipants().values()) {
            GameTeam.Color color = participant.getTeamColor();

            StringBuilder sb = new StringBuilder();

            sb.append(color.getChatColor());
            sb.append(ChatColor.BOLD);
            sb.append(color.toString().charAt(0));
            sb.append(" &f");
            sb.append(color.getName());
            sb.append(": ");
            sb.append(color.getChatColor()).append(ChatColor.BOLD);
            sb.append(participant.isRespawn() ? "✔" : (participant.isCurrentlyPlaying() ? "1" : "✘"));

            lines.add(sb.toString());
        }

        switch(getState()) {
            case STARTING:
                lines.add(" ");
                lines.add("&6Minigame: &fFireball Blitz");
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                lines.add("&6Players: &f" + this.getCurrentPlaying().size());
                break;
            case ACTIVE:
                if(profile.isSidebarShowDuration()) {
                    lines.add(" ");
                    lines.add("&6Duration: &f" + TimeUtil.get(getStarted()));
                }
                break;
            case ENDED:
                lines.add(" ");
                lines.add("&6Winner: &f" + getWinner().getName());
                break;
        }

        return lines;
    }

    @Override
    public void eliminate(Player player, boolean leftGame) {
        super.eliminate(player, leftGame);

        if(getCurrentPlaying().size() < 2) {
            this.end();
        }

        GameParticipant participant = getParticipants().get(player.getUniqueId());
        if(participant.getAttacker() == null) return;

        GameParticipant attacker = getAlive().get(participant.getAttacker());
        if(attacker == null) return;

        PlayerInventory inventory = attacker.getPlayer().getInventory();

        ItemStack fireball = new ItemStack(Material.FIREBALL, 2);
        ItemMeta meta = fireball.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Fireball");
        meta.spigot().setUnbreakable(true);
        fireball.setItemMeta(meta);

        inventory.addItem(fireball);
        inventory.addItem(new ItemStack(Material.TNT));
    }

    @Override
    public void initialize() {
        if(getArena() == null) {
            setArena(getPlugin().getArenaManager().selectRandomArena(Arena.Type.MINIGAME_FIREBALL_BLITZ));
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

            ArenaPosition position = arena.getPositions().get(participant.getTeamColor().toString().toLowerCase() + "spawn");
            participant.setSpawnLocation(position.getLocation());

            p.teleport(position.getLocation());
            participant.getProfile().givePlayerItems();
        }

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

        Bukkit.getScheduler().runTaskLater(getPlugin(), new TeleportFix(this), 1);

        startingTimer(5);
    }

    @Override
    public GameParticipant determineWinner() {
        GameParticipant winner = getCurrentPlaying().values().stream().findFirst().orElse(null);
        setWinner(winner);
        return winner;
    }
}
