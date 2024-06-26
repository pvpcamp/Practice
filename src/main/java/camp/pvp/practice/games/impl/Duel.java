package camp.pvp.practice.games.impl;

import camp.pvp.practice.arenas.Arena;
import camp.pvp.practice.games.Game;
import camp.pvp.practice.games.GameParticipant;
import camp.pvp.practice.games.PostGameInventory;
import camp.pvp.practice.games.tasks.TeleportFix;
import camp.pvp.practice.kits.GameKit;
import camp.pvp.practice.profiles.GameProfile;
import camp.pvp.practice.Practice;
import camp.pvp.practice.arenas.ArenaPosition;
import camp.pvp.practice.profiles.GameProfileManager;
import camp.pvp.practice.profiles.stats.MatchRecord;
import camp.pvp.practice.profiles.stats.ProfileStatistics;
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

import java.util.*;

public class Duel extends Game {

    public @Setter @Getter GameQueue.Type queueType;

    public Duel(Practice plugin, UUID uuid) {
        super(plugin, uuid);
    }

    @Override
    public void initialize() {
        if(getArena() == null) {
            setArena(getPlugin().getArenaManager().selectRandomArena(getKit()));
        }

        if(getArena() == null) {
            for(Player p : getAlivePlayers()) {
                GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfiles().get(p.getUniqueId());
                p.sendMessage(ChatColor.RED + "There are no arenas currently available for the ladder selected. Please notify a staff member.");
                profile.setGame(null);
                profile.playerUpdate(true);
            }
            return;
        }

        Arena arena = getArena();

        arena.prepare();

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
                p.sendMessage(Colors.get(" &7● &6Kit: &f" + getKit().getGameKit().getDisplayName()));
                p.sendMessage(Colors.get(" &7● &6Map: &f" + Colors.get(arena.getDisplayName())));
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
                    participant.setSpawnLocation(location);
                    participant.getProfile().givePlayerItems();
                }
                position++;
            }

            getPlugin().getGameProfileManager().updateGlobalPlayerVisibility();

            Bukkit.getScheduler().runTaskLater(getPlugin(), new TeleportFix(this), 1);

            startingTimer(3);
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
        setEnded(new Date());
        setState(State.ENDED);

        if(getStarted() == null) {
            setStarted(new Date());
            getStartingTimer().cancel();
        }
        
        for(GameParticipant participant : getCurrentPlaying().values()) {
            Player player = participant.getPlayer();
            PostGameInventory pgi = new PostGameInventory(UUID.randomUUID(), participant, player.getInventory().getContents(), player.getInventory().getArmorContents());
            participant.setPostGameInventory(pgi);
            getPlugin().getGameManager().getPostGameInventories().put(pgi.getUuid(), pgi);

            if(participant.getRespawnTask() != null) participant.getRespawnTask().cancel();
        }

        GameParticipant winnerParticipant = null, loserParticipant = null;
        GameProfile winnerProfile = null, loserProfile = null;
        PostGameInventory winnerInventory = null, loserInventory = null;

        for(Map.Entry<UUID, GameParticipant> entry : this.getParticipants().entrySet()) {
            GameParticipant participant = entry.getValue();

            boolean alive = participant.isCurrentlyPlaying();
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

        sendLunarWinTitle(winnerParticipant.getPlayer());
        
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

        int winnerElo = 0, loserElo = 0, difference = 0;

        GameKit kit = getKit().getGameKit();

        ProfileStatistics winnerStatistics = winnerProfile.getProfileStatistics();
        ProfileStatistics loserStatistics = loserProfile.getProfileStatistics();

        if(queueType.equals(GameQueue.Type.RANKED)) {

            winnerElo = winnerStatistics.getElo(kit);
            loserElo = loserStatistics.getElo(kit);
            difference = EloCalculator.getEloDifference(winnerElo, loserElo);

            stringBuilder.append(Colors.get("\n &7● &aWinner: &f" + winnerParticipant.getName() + " &7- &a" + (winnerElo + difference) + " +" + difference));
            stringBuilder.append(Colors.get("\n &7● &cLoser: &f" + loserParticipant.getName()+ " &7- &c" + (loserElo - difference) + " -" + difference));

            winnerStatistics.addElo(kit, difference);
            loserStatistics.subtractElo(kit, difference);
        } else {
            stringBuilder.append(Colors.get("\n &7● &6Winner: &f" + winnerParticipant.getName()));
        }

        winnerStatistics.incrementWins(kit, queueType);
        loserStatistics.resetWinStreak(kit, queueType);

        getPlugin().getGameProfileManager().exportStatistics(winnerStatistics, true);
        getPlugin().getGameProfileManager().exportStatistics(loserStatistics, true);

        // Export match record.
        MatchRecord record = new MatchRecord(this, winnerParticipant, loserParticipant, difference, winnerElo, loserElo);
        getPlugin().getGameProfileManager().exportMatchRecord(record);

        for(Player player : this.getAllPlayers()) {
            player.sendMessage(" ");
            player.sendMessage(stringBuilder.toString());
            player.spigot().sendMessage(text);
            player.sendMessage(" ");
        }

        cleanup(3);
    }

    @Override
    public void eliminate(Player player, boolean leftGame) {

        super.eliminate(player, leftGame);

        GameParticipant participant = getParticipants().get(player.getUniqueId());
        GameProfile profile = getPlugin().getGameProfileManager().getLoadedProfile(player.getUniqueId());
        ProfileStatistics statistics = profile.getProfileStatistics();
        statistics.incrementDeaths(getKit().getGameKit(), queueType);

        GameProfile attackerProfile = getPlugin().getGameProfileManager().getLoadedProfile(participant.getAttacker());
        if (attackerProfile != null) {
            attackerProfile.getProfileStatistics().incrementKills(getKit().getGameKit(), queueType);
        }

        if (getTournament() != null) {
            getTournament().eliminate(player);
        }

        if (getCurrentPlaying().size() < 2) {
            end();
        }
    }

    @Override
    public GameParticipant createParticipant(Player player) {
        return new GameParticipant(player.getUniqueId(), player.getName());
    }

    @Override
    public List<String> getScoreboard(GameProfile profile) {
        List<String> lines = new ArrayList<>();
        GameParticipant self = getParticipants().get(profile.getUuid());
        GameParticipant opponent = null;

        boolean showInGame = profile.isSidebarInGame(),
                showCps = profile.isSidebarShowCps(),
                showDuration = profile.isSidebarShowDuration(),
                showPing = profile.isSidebarShowPing();

        for(GameParticipant p : getCurrentPlaying().values()) {
            if(p.getUuid() != self.getUuid()) {
                opponent = p;
                break;
            }
        }

        switch(getState()) {
            case STARTING:
                lines.add("&6Kit: &f" + getKit().getGameKit().getDisplayName());
                lines.add("&6Arena: &f" + getArena().getDisplayName());
                lines.add("&6Opponent: &f" + opponent.getName());
                break;
            case ACTIVE:
                if(showInGame) {
                    int ping, enemyPing;
                    ping = PlayerUtils.getPing(self.getPlayer());

                    boolean show = false;

                    if(getKit().isBedwars()) {
                        List<GameParticipant> pList = new ArrayList<>(getCurrentPlaying().values());
                        GameParticipant blue = pList.get(0);
                        GameParticipant red = pList.get(1);

                        lines.add("&9B &fBlue: &9&l" + (blue.isRespawn() ? "✔" : "1") + " " + (blue.equals(self) ? "&7YOU" : ""));
                        lines.add("&cR &fRed: &c&l" + (red.isRespawn() ? "✔" : "1") + " " + (red.equals(self) ? "&7YOU" : ""));
                        lines.add(" ");
                    }

                    if (getKit().isBoxing()) {
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

                    if(showDuration) {
                        show = true;
                        lines.add("&6Duration: &f" + TimeUtil.get(new Date(), getStarted()));
                    }

                    if(showPing) {
                        show = true;
                        if (opponent == null || opponent.getPlayer() == null) {
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
                            GameProfile opponentProfile = getPlugin().getGameProfileManager().getLoadedProfile(opponent.getUuid());
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

        if(!getState().equals(State.ENDED)) {

            if(getKit().isBedwars()) {
                List<GameParticipant> pList = new ArrayList<>(getCurrentPlaying().values());
                GameParticipant blue = pList.get(0);
                GameParticipant red = pList.get(1);

                lines.add("&9B &fBlue: &9&l" + (blue.isRespawn() ? "✔" : "1"));
                lines.add("&cR &fRed: &c&l" + (red.isRespawn() ? "✔" : "1"));
                lines.add(" ");
            }

            lines.add("&6Players:");

            for (GameParticipant participant : getParticipants().values()) {
                if (participant.isAlive()) {
                    Player player = participant.getPlayer();
                    if (getKit().isBoxing()) {
                        lines.add("&7> &f" + participant.getName() + " &7(" + participant.getHits() + ")");
                    } else {
                        if (!queueType.equals(GameQueue.Type.RANKED) || getKit().isShowHealthBar()) {
                            lines.add("&7> &f" + participant.getName() + " &c" + Math.round(player.getHealth()) + " ❤");
                        } else {
                            lines.add("&7> &f" + participant.getName() + "&c ❤");
                        }
                    }
                } else {
                    lines.add("&4> &c&m" + participant.getName());
                }
            }
        }

        switch(getState()) {
            case STARTING:
                lines.add(" ");
                lines.add("&6Kit: &f" + getKit().getGameKit().getDisplayName());
                lines.add("&6Arena: &f" + getArena().getDisplayName());
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
                if(getAlivePlayers().size() == 1) {
                    lines.add("&6Winner: &f" + getAlivePlayers().get(0).getName());
                }

                if(showDuration) {
                    lines.add("&6Duration: &f&n" + TimeUtil.get(getEnded(), getStarted()));
                }
                break;
        }

        return lines;
    }

    @Override
    public String getScoreboardTitle() {
        return "Duel";
    }
}
