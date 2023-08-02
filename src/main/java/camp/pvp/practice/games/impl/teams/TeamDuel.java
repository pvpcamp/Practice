package camp.pvp.practice.games.impl.teams;

import camp.pvp.practice.games.GameTeam;
import camp.pvp.practice.games.tasks.EndingTask;
import camp.pvp.practice.games.tasks.StartingTask;
import camp.pvp.practice.parties.Party;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.GameSpectator;
import camp.pvp.practice.games.PostGameInventory;
import camp.pvp.practice.games.impl.teams.TeamGame;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.TimeUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TeamDuel extends TeamGame {

    public TeamDuel(Practice plugin, UUID uuid) {
        super(plugin, uuid);
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
                lines.add("&6Kit: &f" + kit.getDisplayName());
                lines.add("&6Arena: &f" + arena.getDisplayName());
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

                if(showDuration) {
                    lines.add("&6Duration: &f" + TimeUtil.get(new Date(), getStarted()));
                }

                if(showPing) {
                    lines.add("&6Your Ping: &f" + ping + " ms");
                }

                if(showCps) {
                    lines.add("&6Your CPS: &f" + profile.getCps());
                }

                if(showPing || showCps || showDuration) {
                    lines.add("");
                }

                lines.add(friendlyTeam.getColor().getChatColor() + "Alive Teammates:");

                int i = 0;
                for(GameParticipant participant : friendlyTeam.getAliveParticipants().values()) {
                    if(i < 5) {
                        Player player = participant.getPlayer();
                        lines.add(" &f" + participant.getName() + " &c" + Math.round(player.getHealth()) + " ❤");
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
                lines.add("&6Kit: &f" + kit.getDisplayName());
                lines.add("&6Arena: &f" + arena.getDisplayName());
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
    public void start() {
        List<Arena> list = new ArrayList<>();
        if(getArena() == null) {
            for(Arena a : getPlugin().getArenaManager().getArenas()) {
                if(a.isEnabled()) {
                    if(a.getType().equals(Arena.Type.HCF_TEAMFIGHT) && kit.getArenaTypes().contains(Arena.Type.DUEL_HCF)) {
                        list.add(a);
                    } else if(kit.getArenaTypes().contains(a.getType())) {
                        list.add(a);
                    }
                }
            }

            Collections.shuffle(list);
            this.setArena(list.get(0));
        }

        if(arena == null) {
            for(Player p : getAlivePlayers()) {
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(p.getUniqueId());
                p.sendMessage(ChatColor.RED + "There are no arenas currently available for the ladder selected. Please notify a staff member.");
                profile.setGame(null);
                profile.playerUpdate(true);
            }
            return;
        }

        for(Party party : this.getParties()) {
            party.setGame(this);
        }

        this.setState(State.STARTING);

        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append("\n&6&lTeam duel starting in 5 seconds.");
        sb.append("\n &7● &6Kit: " + getKit().getColor() + getKit().getDisplayName());
        sb.append("\n &7● &6Map: &f" + arena.getDisplayName());

        ArenaPosition blueSpawn = arena.getPositions().get("spawn1");
        List<GameParticipant> blueParticipants = new ArrayList<>(getBlue().getParticipants().values());
        sb.append("\n &7● &9Blue Team: &f");
        int blueCount = 0;
        while (blueCount != getBlue().getParticipants().size()) {
            GameParticipant participant = blueParticipants.get(0);
            GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(participant.getUuid());
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
            profile.givePlayerItems();
        }

        ArenaPosition redSpawn = arena.getPositions().get("spawn2");
        List<GameParticipant> redParticipants = new ArrayList<>(getRed().getParticipants().values());
        sb.append("\n &7● &cRed Team: &f");
        int redCount = 0;
        while (redCount != getRed().getParticipants().size()) {
            GameParticipant participant = redParticipants.get(0);
            GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(participant.getUuid());
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
            profile.givePlayerItems();
        }

        sb.append("\n ");

        this.announce(sb.toString());

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

        this.startingTimer = new StartingTask(this, 5).runTaskTimer(this.getPlugin(), 20, 20);
    }

    @Override
    public void end() {
        if(this.getState().equals(State.ENDED)) {
            return;
        }

        this.setEnded(new Date());
        this.setState(State.ENDED);

        if(getStarted() == null) {
            setStarted(new Date());
            getStartingTimer().cancel();
        }

        for(Party party : this.getParties()) {
            party.setGame(null);
        }

        GameTeam winningTeam;
        if(getBlue().isEliminated()) {
            winningTeam = getRed();
        } else {
            winningTeam = getBlue();
        }

        for(GameParticipant participant : getAlive().values()) {
            Player player = participant.getPlayer();
            PostGameInventory pgi = new PostGameInventory(UUID.randomUUID(), participant, player.getInventory().getContents(), player.getInventory().getArmorContents());
            participant.setPostGameInventory(pgi);
            getPlugin().getGameManager().addInventory(pgi);
        }

        List<TextComponent> components = new ArrayList<>();
        for(GameParticipant p : this.getParticipants().values()) {
            TextComponent text = new TextComponent(ChatColor.WHITE + p.getName());
            text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/postgameinventory " + p.getPostGameInventory().getUuid().toString()));
            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to view " + ChatColor.WHITE + p.getName() + "'s " + ChatColor.GREEN + "inventory.").create()));
            components.add(text);
        }

        // Match Summary Message
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" ");
        stringBuilder.append(Colors.get("&6&lMatch ended."));
        if(winningTeam != null) {
            stringBuilder.append(Colors.get("\n &7● &6Winning Team: " + winningTeam.getColor().getChatColor() + winningTeam.getColor().getName()));
        }

        TextComponent text = new TextComponent(ChatColor.GRAY + " ● " + ChatColor.GOLD + "Inventories: ");
        final int size = components.size();
        int x = 0;
        while(x != size) {
            TextComponent t = components.get(0);
            text.addExtra(t);
            components.remove(t);
            x++;
            if(x == size) {
                text.addExtra(new TextComponent(ChatColor.GRAY + "."));
            } else {
                text.addExtra(new TextComponent(ChatColor.GRAY + ", "));
            }
        }

        for(Player player : this.getAllPlayers()) {
            player.sendMessage(" ");
            player.sendMessage(stringBuilder.toString());
            player.spigot().sendMessage(text);
            player.sendMessage(" ");
        }

        this.endingTimer = Bukkit.getScheduler().runTaskLater(getPlugin(), new EndingTask(this), 100);
    }
}
