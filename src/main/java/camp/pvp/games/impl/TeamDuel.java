package camp.pvp.games.impl;

import camp.pvp.Practice;
import camp.pvp.arenas.Arena;
import camp.pvp.arenas.ArenaPosition;
import camp.pvp.cooldowns.PlayerCooldown;
import camp.pvp.games.GameParticipant;
import camp.pvp.games.GameSpectator;
import camp.pvp.games.GameTeam;
import camp.pvp.games.PostGameInventory;
import camp.pvp.games.impl.teams.TeamGame;
import camp.pvp.profiles.GameProfile;
import camp.pvp.profiles.GameProfileManager;
import camp.pvp.utils.Colors;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TeamDuel extends TeamGame {

    protected TeamDuel(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        return null;
    }

    @Override
    public List<String> getSpectatorScoreboard(GameProfile profile) {
        return null;
    }

    @Override
    public void eliminate(Player player, boolean leftGame) {
        super.eliminate(player, leftGame);
        int teamsWithPlayersLeft = 0;
        for(GameTeam team : getTeams()) {
            if(team.getAliveParticipants().size() > 0) {
                teamsWithPlayersLeft++;
            }
        }

        if(teamsWithPlayersLeft < 2) {
            this.end();
        }
    }

    @Override
    public void start() {
        List<Arena> list = new ArrayList<>();
        if(getArena() == null) {
            for(Arena a : getPlugin().getArenaManager().getArenas()) {
                if(a.isEnabled()) {
                    if(kit.getArenaType().equals(Arena.Type.DUEL_TEAMS)) {
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
                profile.playerUpdate();
            }
            return;
        }

        Map<Player, Location> locations = new HashMap<>();

        Map<String, ArenaPosition> positions = arena.getPositions();
        ArenaPosition blueSpawn = positions.get("blue"),
                redSpawn = positions.get("red"),
                yellowSpawn = positions.get("yellow"),
                whiteSpawn = positions.get("white");


        this.setState(State.STARTING);

        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append("\n&6&lTeam duel starting in 5 seconds.");
        sb.append("\n &7● &6Map: &f" + arena.getDisplayName());

        for(GameTeam team : this.getTeams()) {
            ArenaPosition spawnPosition = arena.getPositions().get(team.getColor().toString().toLowerCase());
            List<GameParticipant> p = new ArrayList<>(team.getParticipants().values());
            sb.append("\n &7● " + team.getColor().getChatColor() + team.getColor().toString() + ": &f");
            int x = 0;
            while (x != team.getParticipants().size()) {
                GameParticipant participant = p.get(0);
                Player player = participant.getPlayer();
                sb.append(ChatColor.WHITE + participant.getName());

                p.remove(participant);
                x++;
                if (x == team.getParticipants().size()) {
                    sb.append("&7.");
                } else {
                    sb.append("&7, ");
                }

                player.teleport(spawnPosition.getLocation());
                this.getKit().apply(player);
            }
        }

        sb.append("\n ");

        this.announce(sb.toString());

        getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

        this.startingTimer = new BukkitRunnable() {
            int i = 5;
            public void run() {
                if (i == 0) {
                    for(Player p : TeamDuel.this.getAllPlayers()) {
                        if(p != null) {
                            p.playSound(p.getLocation(), Sound.NOTE_PIANO, 1, 12);
                            p.sendMessage(ChatColor.GREEN + "The game has started, good luck!");
                        }
                    }

                    TeamDuel duel = TeamDuel.this;
                    duel.setStarted(new Date());
                    duel.setState(State.ACTIVE);

                    this.cancel();
                } else {
                    if (i > 0) {
                        for (Player p : TeamDuel.this.getAllPlayers()) {
                            p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                            p.sendMessage(ChatColor.GREEN.toString() + i + "...");
                        }
                    }

                    i -= 1;
                }
            }
        }.runTaskTimer(this.getPlugin(), 20, 20);
    }

    @Override
    public void end() {
        GameProfileManager gpm = getPlugin().getGameProfileManager();
        this.setEnded(new Date());
        this.setState(State.ENDED);

        if(getStarted() == null) {
            setStarted(new Date());
            getStartingTimer().cancel();
        }

        GameTeam winningTeam = null;
        for(GameTeam team : getTeams()) {
            if(team.getAliveParticipants().size() > 0) {
                winningTeam = team;
                break;
            }
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
            stringBuilder.append(Colors.get("&6Winning Team: " + winningTeam.getColor().getChatColor() + winningTeam.toString()));
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

        this.endingTimer = Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            for(Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                GameParticipant participant = entry.getValue();
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(entry.getKey());

                if(player != null) {
                    if(entry.getValue().isAlive()) {
                        participant.clearCooldowns();

                        profile.setGame(null);
                        profile.playerUpdate();
                    }
                }
            }

            for(GameSpectator spectator : new ArrayList<>(this.getSpectators().values())) {
                Player player = Bukkit.getPlayer(spectator.getUuid());
                this.spectateEnd(player);
            }

            this.clearEntities();
        }, 60);
    }
}
