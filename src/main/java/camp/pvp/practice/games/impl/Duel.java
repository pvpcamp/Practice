package camp.pvp.practice.games.impl;

import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.GameSpectator;
import camp.pvp.practice.games.PostGameInventory;
import camp.pvp.practice.games.tasks.EndingTask;
import camp.pvp.practice.games.tasks.StartingTask;
import camp.pvp.practice.games.tasks.TeleportFixTask;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.cooldowns.PlayerCooldown;
import camp.pvp.practice.kits.DuelKit;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.profiles.stats.ProfileELO;
import camp.pvp.practice.queue.GameQueue;
import camp.pvp.practice.utils.Colors;
import camp.pvp.practice.utils.EloCalculator;
import camp.pvp.practice.utils.PlayerUtils;
import camp.pvp.practice.utils.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Duel extends Game {

    public @Setter @Getter GameQueue.Type queueType;

    public Duel(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public void start() {

        List<Arena> list = new ArrayList<>();
        if(getArena() == null) {
            for(Arena a : getPlugin().getArenaManager().getArenas()) {
                if(a.isEnabled()) {
                    if(kit.getArenaTypes().contains(a.getType())) {
                        list.add(a);
                    }
                }
            }

            Collections.shuffle(list);
            if(!list.isEmpty()) {
                this.setArena(list.get(0));
            }
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

        Map<Player, Location> locations = new HashMap<>();

        Map<String, ArenaPosition> positions = arena.getPositions();
        ArenaPosition pos1 = positions.get("spawn1"), pos2 = positions.get("spawn2");

        if(pos1 != null && pos2 != null) {

            this.setState(State.STARTING);

            StringBuilder stringBuilder = new StringBuilder();
            List<Player> players = new ArrayList<>(this.getAlivePlayers());

            int s = 0;
            while(s != this.getAlive().size()) {
                Player p = players.get(0);
                stringBuilder.append(ChatColor.WHITE + p.getName());

                players.remove(p);
                s++;
                if(s == this.getAlivePlayers().size()) {
                    stringBuilder.append(ChatColor.GRAY + ".");
                } else {
                    stringBuilder.append(ChatColor.GRAY + ", ");
                }
            }

            for(Player p : this.getAllPlayers()) {
                p.sendMessage(" ");
                p.sendMessage(Colors.get("&6&lDuel starting in 3 seconds."));
                p.sendMessage(Colors.get(" &7● &6Mode: &f" + this.queueType.toString()));
                p.sendMessage(Colors.get(" &7● &6Kit: &f" + kit.getColor() + kit.getDisplayName()));
                p.sendMessage(Colors.get(" &7● &6Map: &f" + Colors.get(getArena().getDisplayName())));
                p.sendMessage(Colors.get(" &7● &6Participants: &f" + stringBuilder));
                p.sendMessage(" ");
            }

            int position = 1;
            for (Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
                GameParticipant participant = entry.getValue();
                Player p = Bukkit.getPlayer(entry.getKey());
                Location location = null;
                if (position == 1) {
                    location = pos1.getLocation();
                }

                if (position == 2) {
                    location = pos2.getLocation();
                }

                if (p != null) {
                    locations.put(p, location);
                    p.teleport(locations.get(p));

                    getPlugin().getGameProfileManager().getLoadedProfiles().get(entry.getKey()).givePlayerItems();
                }
                position++;
            }

            getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

            new TeleportFixTask(this).run();
            this.startingTimer = new StartingTask(this, 3).runTaskTimer(this.getPlugin(), 20, 20);
        } else {
            for(Player p : getAlivePlayers()) {
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(p.getUniqueId());
                p.sendMessage(ChatColor.RED + "The arena " + arena.getName() + " does not have valid spawn points, please notify a staff member.");
                profile.setGame(null);
                profile.playerUpdate(true);
            }
        }
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
        
        for(GameParticipant participant : getAlive().values()) {
            Player player = participant.getPlayer();
            PostGameInventory pgi = new PostGameInventory(UUID.randomUUID(), participant, player.getInventory().getContents(), player.getInventory().getArmorContents());
            participant.setPostGameInventory(pgi);
            getPlugin().getGameManager().getPostGameInventories().put(pgi.getUuid(), pgi);
        }

        GameParticipant winnerParticipant = null, loserParticipant = null;
        GameProfile winnerProfile = null, loserProfile = null;
        PostGameInventory winnerInventory = null, loserInventory = null;

        for(Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
            GameParticipant participant = entry.getValue();

            boolean alive = participant.isAlive();
            if(alive) {
                winnerParticipant = participant;
                winnerProfile = gpm.getLoadedProfiles().get(entry.getKey());
                winnerInventory = participant.getPostGameInventory();
            } else {
                loserParticipant = participant;
                loserProfile = gpm.getLoadedProfiles().get(entry.getKey());
                loserInventory = participant.getPostGameInventory();
            }
        }
        
        winnerInventory.setOpponentInventory(loserParticipant, loserInventory);
        loserInventory.setOpponentInventory(winnerParticipant, winnerInventory);

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

        if(queueType.equals(GameQueue.Type.RANKED)) {
            ProfileELO winnerEloProfile, loserEloProfile;
            winnerEloProfile = winnerProfile.getProfileElo();
            loserEloProfile = loserProfile.getProfileElo();

            int winnerElo = winnerEloProfile.getRatings().get(kit);
            int loserElo = loserEloProfile.getRatings().get(kit);
            int difference = EloCalculator.getEloDifference(winnerElo, loserElo);

            stringBuilder.append(Colors.get("\n &7● &aWinner: &f" + winnerParticipant.getName() + " &7- &a" + (winnerElo + difference) + " +" + difference));
            stringBuilder.append(Colors.get("\n &7● &cLoser: &f" + loserParticipant.getName()+ " &7- &c" + (loserElo - difference) + " -" + difference));

            winnerEloProfile.getRatings().put(kit, EloCalculator.getNewWinnerElo(winnerElo, loserElo));
            loserEloProfile.getRatings().put(kit, EloCalculator.getNewLoserElo(winnerElo, loserElo));

            gpm.exportElo(winnerEloProfile, true);
            gpm.exportElo(loserEloProfile, true);
        } else {
            stringBuilder.append(Colors.get("\n &7● &6Winner: &f" + winnerParticipant.getName()));
        }

        for(Player player : this.getAllPlayers()) {
            player.sendMessage(" ");
            player.sendMessage(stringBuilder.toString());
            player.spigot().sendMessage(text);
            player.sendMessage(" ");
        }

        this.endingTimer = Bukkit.getScheduler().runTaskLater(getPlugin(), new EndingTask(this), 60);
    }

    @Override
    public void eliminate(Player player, boolean leftGame) {
        super.eliminate(player, leftGame);

        if(getTournament() != null) {
            getTournament().eliminate(player);
        }

        if(this.getAlive().size() < 2) {
            this.end();
        }
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();
        GameParticipant self = getAlive().get(profile.getUuid());
        GameParticipant opponent = null;

        boolean showInGame = profile.isSidebarInGame(),
                showCps = profile.isSidebarShowCps(),
                showDuration = profile.isSidebarShowDuration(),
                showPing = profile.isSidebarShowPing();

        for(GameParticipant p : getAlive().values()) {
            if(p.getUuid() != self.getUuid()) {
                opponent = p;
                break;
            }
        }

        switch(getState()) {
            case STARTING:
                lines.add("&6Kit: &f" + kit.getDisplayName());
                lines.add("&6Arena: &f" + arena.getDisplayName());
                lines.add("&6Opponent: &f" + opponent.getName());
                break;
            case ACTIVE:
                if(showInGame) {
                    int ping = 0, enemyPing = 0;
                    ping = PlayerUtils.getPing(self.getPlayer());

                    boolean show = false;

                    if(showDuration) {
                        show = true;
                        lines.add("&6Duration: &f" + TimeUtil.get(new Date(), getStarted()));
                    }

                    if (kit.equals(DuelKit.BOXING)) {
                        show = true;
                        StringBuilder lineBuilder = new StringBuilder();
                        lineBuilder.append("&6Hits: &f" + self.getHits());
                        if(opponent != null) {
                            long difference = self.getHits() - opponent.getHits();

                            lineBuilder.append(" &7┃ &f" + opponent.getHits());

                            if (difference == 0) {
                                lineBuilder.append(" &7(±0)");
                            } else if (difference > 0) {
                                lineBuilder.append(" &e(+" + difference + ")");
                            } else {
                                lineBuilder.append(" &c(" + difference + ")");
                            }
                        }

                        lines.add(lineBuilder.toString());
                    }

                    if(showPing) {
                        show = true;
                        if (opponent == null) {
                            lines.add("&6Ping: &f" + ping + " ms");
                        } else {
                            enemyPing = PlayerUtils.getPing(opponent.getPlayer());
                            lines.add("&6Ping: &f" + ping + " ms &7┃ &f" + enemyPing + " ms");
                        }
                    }

                    if(showCps) {
                        show = true;
                        if(opponent == null) {
                            lines.add("&6CPS: &f" + profile.getCps());
                        } else {
                            GameProfile opponentProfile = getPlugin().getGameProfileManager().getLoadedProfiles().get(opponent.getUuid());
                            lines.add("&6CPS: &f" + profile.getCps() + " &7┃ &f" + opponentProfile.getCps());
                        }
                    }

                    if(!show) {
                        return null;
                    }
                } else {
                    return null;
                }
                break;
            case ENDED:
                lines.add("&6&lYou win!");

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

        boolean showInGame = profile.isSidebarInGame(),
                showCps = profile.isSidebarShowCps(),
                showDuration = profile.isSidebarShowDuration(),
                showPing = profile.isSidebarShowPing();

        lines.add("&6Players:");

        for(GameParticipant participant : getParticipants().values()) {
            if(participant.isAlive()) {
                Player player = participant.getPlayer();
                if(kit.equals(DuelKit.BOXING)) {
                    lines.add(" &f" + participant.getName() + " &7(" + participant.getHits() + ")");
                } else {
                    if (!queueType.equals(GameQueue.Type.RANKED)) {
                        lines.add(" &f" + participant.getName() + " &c" + Math.round(player.getHealth()) + " ❤");
                    } else {
                        lines.add(" &f" + participant.getName() + "&c ❤");
                    }
                }
            } else {
                lines.add(" &4X &c&m" + participant.getName());
            }
        }

        switch(getState()) {
            case STARTING:
                lines.add(" ");
                lines.add("&6Kit: &f" + kit.getDisplayName());
                lines.add("&6Arena: &f" + arena.getDisplayName());
                break;
            case ACTIVE:
                if(!showInGame) {
                    return null;
                }

                if(showDuration) {
                    lines.add(" ");
                    lines.add("&6Duration: &f" + TimeUtil.get(new Date(), getStarted()));
                }
                break;
            case ENDED:
                if(showDuration) {
                    lines.add(" ");
                    lines.add("&6Duration: &f&n" + TimeUtil.get(getEnded(), getStarted()));
                }
                break;
        }

        return lines;
    }
}
